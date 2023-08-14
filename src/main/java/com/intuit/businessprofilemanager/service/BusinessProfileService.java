package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.exception.EntityNotFoundException;
import com.intuit.businessprofilemanager.exception.InvalidDataException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.repository.BusinessProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intuit.businessprofilemanager.utils.ProfileUtil.*;

@Service
@Slf4j
public class BusinessProfileService implements IBusinessProfileService {
    private final BusinessProfileRepository repository;

    public BusinessProfileService(BusinessProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public String createProfile(BusinessProfile profile, List<String> products) {
        ProfileEntity profileEntity = repository.saveAndFlush(getProfileEntity(profile, products));
        return profileEntity.getId().toString();
    }

    @Override
    public BusinessProfileEntity getProfile(String profileId) {
        ProfileEntity profileEntity = repository.getReferenceById(Long.valueOf(profileId));
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

    }

    @Override
    public BusinessProfileEntity updateProfile(String profileId, BusinessProfile profile) {
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
                throw new EntityNotFoundException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidDataException();
        }
    }

    @Override
    public boolean deleteProfile(String profileId) {
        ProfileEntity profileEntity = repository.getReferenceById(Long.valueOf(profileId));
        if (profileEntity != null) {
            repository.deleteById(profileEntity.getId());
            return true;
        }
        return false;
    }

    @Override
    public BusinessProfile updateSubscription(String profileId, List<String> subscriptions) {
        ProfileEntity profileEntity = repository.getReferenceById(Long.valueOf(profileId));
        ProfileEntity entity = repository.saveAndFlush(profileEntity);
        List<SubscriptionProducts> subscriptionProducts = Collections.singletonList(SubscriptionProducts.builder()
                .products(subscriptions)
                .build());
        List<TaxIdentifier> taxIdentifiers =
                entity.getTaxIdentifiers().stream().map(taxIdentifiersEntity -> TaxIdentifier.builder().taxIdentifierNo(taxIdentifiersEntity.getTaxIdentifierNo()).taxIdentifierType(taxIdentifiersEntity.getTaxIdentifierType()).build()).collect(Collectors.toList());
        return BusinessProfile.builder()
                .legalName(entity.getLegalName())
                .companyName(entity.getCompanyName())
                .id(String.valueOf(entity.getId()))
                .businessAddress(getAddress(entity.getBusinessAddress()))
                .legalAddress(getAddress(entity.getLegalAddress()))
                .taxIdentifiers(taxIdentifiers)
                .website(entity.getWebsite())
                .email(entity.getEmail())
                .subscriptionProducts(subscriptionProducts)
                .build();
    }
}
