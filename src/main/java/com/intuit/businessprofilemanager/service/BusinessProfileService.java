package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.exception.DataNotFoundException;
import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.exception.RepositoryException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.repository.BusinessProfileRepository;
import com.intuit.businessprofilemanager.utils.ProfileUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.intuit.businessprofilemanager.utils.ProfileUtil.*;

@Service
@Slf4j
public class BusinessProfileService implements IBusinessProfileService {
    private final IValidationService validationService;
    private final BusinessProfileRepository repository;

    public BusinessProfileService(IValidationService validationService, BusinessProfileRepository repository) {
        this.validationService = validationService;
        this.repository = repository;
    }

    /**
     * @param profile  The business profile that needs to be subscribed.
     * @param products products list for which the profile needs to be subscribed for.
     * @return The unique identifier for each subscription.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.create-profile.timer")
    public Long createProfile(BusinessProfile profile, List<String> products) {
        ProfileEntity profileEntity;
        try {
            profileEntity = repository.saveAndFlush(getProfileEntity(profile, products));
        } catch (PersistenceException e) {
            String message = "Failure in persisting profile details";
            log.error(message);
            throw new RepositoryException(message);
        }
        return profileEntity.getId();
    }

    /**
     * Retrieves the business profile details for the subscribed profileId.
     *
     * @param profileId The unique identifier of the business profile that is subscribed.
     * @return Business profile details corresponding to the subscribed profile id.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.get-profile.timer")
    public BusinessProfileData getProfile(Long profileId) {
        ProfileEntity profileEntity;
        try {
            profileEntity = repository.getReferenceById(profileId);
        } catch (EntityNotFoundException e) {
            String message = String.format("The given profileId: %s doesn't exist or profileId is invalid", profileId);
            log.error(message);
            throw new DataNotFoundException(message);
        } catch (PersistenceException e) {
            String msg = String.format("An error occurred while attempting to read profile for profileId: %s.",
                    profileId);
            log.error(msg);
            throw new RepositoryException(msg);
        }
        return getBusinessProfileData(profileId, profileEntity);
    }

    /**
     * Updates the business profile associated with the given profileId and provides details about the updated profile.
     *
     * @param profileId The unique identifier of the business profile that is subscribed.
     * @param request   The business profile that needs to be updated.
     * @return Business profile details corresponding to the subscribed profile id that has been updated.
     * @throws DataValidationException if there's an issue with the provided data.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.update-profile.timer")
    public BusinessProfileData updateProfile(Long profileId, BusinessProfileUpdateRequest request) {
        try {
            ProfileEntity existingProfileEntity = repository.getReferenceById(profileId);
            BusinessProfile profile = ProfileUtil.buildBusinessProfile(request, existingProfileEntity);
            validateProfileWithProducts(profile);
            ProfileEntity profileEntity = buildProfileEntity(profile, existingProfileEntity);
            ProfileEntity updatedProfile = repository.save(profileEntity);
            return buildBusinessProfileData(updatedProfile);
        } catch (EntityNotFoundException e) {
            String message = String.format("The given profileId: %s doesn't exist or profileId is invalid", profileId);
            log.error(message);
            throw new DataNotFoundException(message);
        } catch (PersistenceException e) {
            String msg = String.format("An error occurred while attempting to update profile for profileId: %s.",
                    profileId);
            log.error(msg);
            throw new RepositoryException(msg);
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
    public boolean deleteProfile(Long profileId) {
        try {
            repository.deleteById(profileId);
            return true;
        } catch (PersistenceException e) {
            String msg = String.format("An error occurred while attempting to delete profile for profileId: %s.",
                    profileId);
            log.error(msg);
            throw new RepositoryException(msg);
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
    public BusinessProfile updateSubscription(Long profileId, List<String> tobeSubscribedProducts) {
        ProfileEntity existingProfileEntity;
        try {
            existingProfileEntity = repository.getReferenceById(profileId);
            updateWithNewSubscriptions(tobeSubscribedProducts, existingProfileEntity);

            validateProfileWithProducts(ProfileUtil.buildBusinessProfile(existingProfileEntity,
                    tobeSubscribedProducts));

            Set<SubscriptionEntity> updatedSubscriptionEntities = getUpdatedSubscriptionEntities(tobeSubscribedProducts,
                    existingProfileEntity);
            existingProfileEntity.setSubscriptionEntities(updatedSubscriptionEntities);
            ProfileEntity updatedEntity = repository.save(existingProfileEntity);
            return buildBusinessProfile(profileId, tobeSubscribedProducts, updatedEntity);
        } catch (EntityNotFoundException e) {
            String message = String.format("The given profileId: %s doesn't exist or profileId is invalid", profileId);
            log.error(message);
            throw new DataNotFoundException(message);
        } catch (PersistenceException e) {
            String msg = String.format("An error occurred while attempting to update subscription for profileId: %s.",
                    profileId);
            log.error(msg);
            throw new RepositoryException(msg);
        }
    }

    /**
     * validates against all products and throws exception if validation fails
     *
     * @param profile todo update
     */
    private void validateProfileWithProducts(BusinessProfile profile) {
        List<ValidationResponse> responses = validationService.validate(profile,
                profile.getSubscriptionProducts().getProducts());
        List<ValidationResponse> validationFailedResponses = responses.stream()
                .filter(response -> response.getStatus() == ValidationStatus.FAILED)
                .collect(Collectors.toList());
        if (!validationFailedResponses.isEmpty()) {
            throw new DataValidationException(validationFailedResponses);
        }
    }

    private void updateWithNewSubscriptions(List<String> tobeSubscribedProducts,
                                            ProfileEntity existingProfileEntity) {
        tobeSubscribedProducts.removeAll(existingProfileEntity.getSubscriptionEntities().stream()
                .map(SubscriptionEntity::getProduct)
                .collect(Collectors.toSet())
        );
    }

    private Set<SubscriptionEntity> getUpdatedSubscriptionEntities(List<String> tobeSubscribedProducts,
                                                                   ProfileEntity existingProfileEntity) {
        Set<String> existingProducts = existingProfileEntity.getSubscriptionEntities().stream()
                .map(SubscriptionEntity::getProduct)
                .collect(Collectors.toSet());
        Set<String> tobeSubscribedProductSet = new HashSet<>(tobeSubscribedProducts);
        existingProducts.addAll(tobeSubscribedProductSet);

        return existingProducts.stream()
                .map(product -> SubscriptionEntity.builder().product(product).build())
                .collect(Collectors.toSet());
    }
}
