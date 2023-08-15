package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.exception.DataNotFoundException;
import com.intuit.businessprofilemanager.exception.InvalidDataException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.repository.BusinessProfileRepository;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.intuit.businessprofilemanager.utils.ProfileUtil.*;

@Service
@Slf4j
public class BusinessProfileService implements IBusinessProfileService {
    private final BusinessProfileRepository repository;
    private final AppMetrics metrics;

    public BusinessProfileService(BusinessProfileRepository repository, AppMetrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    /**
     * @param profile  The business profile that needs to be subscribed.
     * @param products products list for which the profile needs to be subscribed for.
     * @return The unique identifier for each subscription.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.create-profile.timer")
    public String createProfile(BusinessProfile profile, List<String> products) {
        ProfileEntity profileEntity = repository.saveAndFlush(getProfileEntity(profile, products));
        return profileEntity.getId().toString();
    }

    /**
     * Retrieves the business profile details for the subscribed profileId.
     *
     * @param profileId The unique identifier of the business profile that is subscribed.
     * @return Business profile details corresponding to the subscribed profile id.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.get-profile.timer")
    public BusinessProfileEntity getProfile(String profileId) {
        ProfileEntity profileEntity;
        try {
            profileEntity = repository.getReferenceById(Long.valueOf(profileId));
            List<SubscriptionProducts> subscriptionProducts =
                    profileEntity.getSubscriptionEntities().stream().map(subscriptionEntity -> SubscriptionProducts.builder().products(Collections.singletonList(subscriptionEntity.getProduct())).build()).collect(Collectors.toList());
            List<TaxIdentifier> taxIdentifiers =
                    profileEntity.getTaxIdentifiers().stream().map(taxIdentifiersEntity -> TaxIdentifier.builder().taxIdentifierNo(taxIdentifiersEntity.getTaxIdentifierNo()).taxIdentifierType(taxIdentifiersEntity.getTaxIdentifierType()).build()).collect(Collectors.toList());
            return BusinessProfileEntity.builder()
                    .profile(BusinessProfile.builder()
                            .id(profileId)
                            .companyName(profileEntity.getCompanyName())
                            .legalName(profileEntity.getLegalName())
                            .email(profileEntity.getEmail())
                            .website(profileEntity.getWebsite())
                            .legalAddress(getAddress(profileEntity.getLegalAddress()))
                            .businessAddress(getAddress(profileEntity.getBusinessAddress()))
                            .taxIdentifiers(taxIdentifiers)
                            .build())
                    .subscribedProducts(subscriptionProducts.stream()
                            .flatMap(subscriptionProduct -> subscriptionProduct.getProducts().stream())
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            log.error("The given profileId: {} doesn't exist or profileId is invalid", profileId);
            metrics.incrementDATA_NOT_FOUND();
            throw new DataNotFoundException();
        }

    }

    /**
     * Updates the business profile associated with the given profileId and provides details about the updated profile.
     *
     * @param profileId The unique identifier of the business profile that is subscribed.
     * @param profile   The business profile that needs to be updated.
     * @return Business profile details corresponding to the subscribed profile id that has been updated.
     * @throws InvalidDataException if there's an issue with the provided data.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.update-profile.timer")
    public BusinessProfileEntity updateProfile(String profileId, BusinessProfile profile) throws InvalidDataException {
        Optional<ProfileEntity> existingProfileEntity;
        try {
            existingProfileEntity = repository.findById(Long.valueOf(profileId));
            if (existingProfileEntity.isPresent()) {
                ProfileEntity updatedProfileEntity = ProfileEntity.builder()
                        .id(existingProfileEntity.get().getId())
                        .legalName(profile.getLegalName())
                        .companyName(profile.getCompanyName())
                        .email(profile.getEmail())
                        .website(profile.getWebsite())
                        .businessAddress(getAddress(profile.getBusinessAddress(), AddressType.BUSINESS))
                        .legalAddress(getAddress(profile.getLegalAddress(), AddressType.LEGAL))
                        .subscriptionEntities(getSubscriptionEntities(profile.getSubscriptionProducts().get(0).getProducts()))
                        .taxIdentifiers(getTaxIdentifiers(profile.getTaxIdentifiers()))
                        .build();
                repository.save(updatedProfileEntity);
                return BusinessProfileEntity.builder()
                        .profile(BusinessProfile.builder()
                                .id(String.valueOf(updatedProfileEntity.getId()))
                                .companyName(updatedProfileEntity.getCompanyName())
                                .legalName(updatedProfileEntity.getLegalName())
                                .website(updatedProfileEntity.getWebsite())
                                .email(updatedProfileEntity.getEmail())
                                .build())
                        .build();
            } else {
                log.error("The profile with the provided ID: {} does not have an existing subscription to any products in the database.", profileId);
                metrics.incrementDATA_NOT_FOUND();
                throw new DataNotFoundException();
            }
        } catch (NumberFormatException e) {
            log.error("The profile with the provided ID: {} is not valid", profileId);
            metrics.incrementINVALID_DATA_EXCEPTION();
            throw new InvalidDataException();
        }
    }

    /**
     * Checks whether the business profile associated with the given profileId has been unsubscribed successfully.
     *
     * @param profileId The unique identifier of the business profile that is unsubscribed.
     * @return True if the profile has been unsubscribed successfully, otherwise false.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.delete-profile.timer")
    public boolean deleteProfile(String profileId) {
        ProfileEntity profileEntity;
        try {
            profileEntity = repository.getReferenceById(Long.valueOf(profileId));
            repository.deleteById(profileEntity.getId());
            return true;
        } catch (Exception e) {
            metrics.incrementDATA_NOT_FOUND();
            throw new DataNotFoundException();
        }
    }

    /**
     * Subscribes the given list of products to the business profile associated with the provided profileId,
     * and returns the updated business profile details.
     *
     * @param profileId              The unique identifier of the business profile that is subscribed.
     * @param tobeSubscribedProducts The list of products that need to be subscribed.
     * @return Business profile details corresponding to the subscribed profile id that has been subscribed with new products.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.update-subscription.timer")
    public BusinessProfile updateSubscription(String profileId, List<String> tobeSubscribedProducts) {
        ProfileEntity existingProfileEntity;
        try {
            existingProfileEntity = repository.getReferenceById(Long.valueOf(profileId));
            Set<String> existingProducts = existingProfileEntity.getSubscriptionEntities().stream()
                    .map(SubscriptionEntity::getProduct)
                    .collect(Collectors.toSet());

            Set<String> tobeSubscribedProductSet = new HashSet<>(tobeSubscribedProducts);
            existingProducts.addAll(tobeSubscribedProductSet);

            Set<SubscriptionEntity> updatedSubscriptionEntities = existingProducts.stream()
                    .map(product -> SubscriptionEntity.builder().product(product).build())
                    .collect(Collectors.toSet());

            existingProfileEntity.setSubscriptionEntities(updatedSubscriptionEntities);

            ProfileEntity updatedEntity = repository.save(existingProfileEntity);
            List<SubscriptionProducts> subscriptionProducts = Collections.singletonList(SubscriptionProducts.builder()
                    .products(tobeSubscribedProducts)
                    .build());
            List<TaxIdentifier> taxIdentifiers =
                    updatedEntity.getTaxIdentifiers().stream().map(taxIdentifiersEntity -> TaxIdentifier.builder().taxIdentifierNo(taxIdentifiersEntity.getTaxIdentifierNo()).taxIdentifierType(taxIdentifiersEntity.getTaxIdentifierType()).build()).collect(Collectors.toList());
            return BusinessProfile.builder()
                    .id(profileId)
                    .legalName(updatedEntity.getLegalName())
                    .companyName(updatedEntity.getCompanyName())
                    .id(String.valueOf(updatedEntity.getId()))
                    .businessAddress(getAddress(updatedEntity.getBusinessAddress()))
                    .legalAddress(getAddress(updatedEntity.getLegalAddress()))
                    .taxIdentifiers(taxIdentifiers)
                    .website(updatedEntity.getWebsite())
                    .email(updatedEntity.getEmail())
                    .subscriptionProducts(subscriptionProducts)
                    .build();
        } catch (Exception e) {
            log.error("The given profileId: {} doesn't exist or profileId is invalid. Exception: {}", profileId, e.getMessage());
            metrics.incrementDATA_NOT_FOUND();
            throw new DataNotFoundException();
        }

    }
}
