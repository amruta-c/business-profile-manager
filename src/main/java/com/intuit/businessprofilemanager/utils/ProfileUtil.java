package com.intuit.businessprofilemanager.utils;

import com.intuit.businessprofilemanager.entity.AddressEntity;
import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.entity.TaxIdentifiersEntity;
import com.intuit.businessprofilemanager.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ProfileUtil {

    public static ProfileEntity getProfileEntity(BusinessProfile profile, List<String> products) {
        return ProfileEntity.builder()
                .companyName(profile.getCompanyName())
                .legalName(profile.getLegalName())
                .email(profile.getEmail())
                .website(profile.getWebsite())
                .businessAddress(getAddress(profile.getBusinessAddress(), AddressType.BUSINESS))
                .legalAddress(getAddress(profile.getLegalAddress(), AddressType.LEGAL))
                .taxIdentifiers(getTaxIdentifiers(profile.getTaxIdentifiers()))
                .subscriptionEntities(getSubscriptionEntities(products))
                .build();
    }

    public static ProfileEntity getProfileEntity(BusinessProfile profile) {
        return ProfileEntity.builder()
                .companyName(profile.getCompanyName())
                .legalName(profile.getLegalName())
                .email(profile.getEmail())
                .website(profile.getWebsite())
                .businessAddress(getAddress(profile.getBusinessAddress(), AddressType.BUSINESS))
                .legalAddress(getAddress(profile.getLegalAddress(), AddressType.LEGAL))
                .taxIdentifiers(getTaxIdentifiers(profile.getTaxIdentifiers()))
                .subscriptionEntities(getSubscriptionEntities(profile.getSubscriptionProducts().getProducts()))
                .build();
    }

    public static Set<SubscriptionEntity> getSubscriptionEntities(List<String> products) {
        return products.stream().map(product -> SubscriptionEntity.builder()
                .product(product)
                .build()).collect(Collectors.toSet());
    }

    public static Set<TaxIdentifiersEntity> getTaxIdentifiers(List<TaxIdentifier> taxIdentifiers) {
        return taxIdentifiers.stream().map(taxIdentifier -> TaxIdentifiersEntity.builder()
                .taxIdentifierNo(taxIdentifier.getTaxIdentifierNo())
                .taxIdentifierType(taxIdentifier.getTaxIdentifierType())
                .build()
        ).collect(Collectors.toSet());
    }

    public static AddressEntity getAddress(Address address, AddressType addressType) {
        return AddressEntity.builder()
                .addressType(addressType.equals(AddressType.LEGAL) ? AddressType.LEGAL : AddressType.BUSINESS)
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .zip(address.getZip())
                .country(address.getCountry())
                .build();
    }

    public static Address getAddress(AddressEntity entity) {
        return Address.builder()
                .line1(entity.getLine1())
                .line2(entity.getLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .zip(entity.getZip())
                .country(entity.getCountry())
                .build();
    }

    public static BusinessProfileData getBusinessProfileData(Long profileId, ProfileEntity profileEntity) {
        List<SubscriptionProducts> subscriptionProducts = getSubscriptionProducts(profileEntity);
        List<TaxIdentifier> taxIdentifiers = buildTaxIdentifiers(profileEntity);
        return BusinessProfileData.builder()
                .profile(buildBusinessProfile(profileId, profileEntity, taxIdentifiers))
                .subscribedProducts(buildSubscribedProducts(subscriptionProducts))
                .build();
    }

    private static List<TaxIdentifier> buildTaxIdentifiers(ProfileEntity profileEntity) {
        return profileEntity.getTaxIdentifiers().stream()
                .map(ProfileUtil::buildTaxIdentifierEntity).collect(Collectors.toList());
    }

    private static List<String> buildSubscribedProducts(List<SubscriptionProducts> subscriptionProducts) {
        return subscriptionProducts.stream()
                .flatMap(subscriptionProduct -> subscriptionProduct.getProducts().stream())
                .collect(Collectors.toList());
    }

    private static BusinessProfile buildBusinessProfile(Long profileId, ProfileEntity profileEntity,
                                                        List<TaxIdentifier> taxIdentifiers) {
        return BusinessProfile.builder()
                .id(profileId)
                .companyName(profileEntity.getCompanyName())
                .legalName(profileEntity.getLegalName())
                .email(profileEntity.getEmail())
                .website(profileEntity.getWebsite())
                .legalAddress(getAddress(profileEntity.getLegalAddress()))
                .businessAddress(getAddress(profileEntity.getBusinessAddress()))
                .taxIdentifiers(taxIdentifiers)
                .build();
    }

    public static TaxIdentifier buildTaxIdentifierEntity(TaxIdentifiersEntity taxIdentifiersEntity) {
        return TaxIdentifier.builder()
                .taxIdentifierNo(taxIdentifiersEntity.getTaxIdentifierNo())
                .taxIdentifierType(taxIdentifiersEntity.getTaxIdentifierType())
                .build();
    }

    private static List<SubscriptionProducts> getSubscriptionProducts(ProfileEntity profileEntity) {
        return profileEntity.getSubscriptionEntities().stream()
                .map(subscriptionEntity -> SubscriptionProducts.builder()
                        .products(Collections.singletonList(subscriptionEntity.getProduct()))
                        .build())
                .collect(Collectors.toList());
    }

    public static BusinessProfileData buildBusinessProfileData(ProfileEntity updatedProfile) {
        return BusinessProfileData.builder()
                .profile(BusinessProfile.builder()
                        .id(updatedProfile.getId())
                        .companyName(updatedProfile.getCompanyName())
                        .legalName(updatedProfile.getLegalName())
                        .website(updatedProfile.getWebsite())
                        .email(updatedProfile.getEmail())
                        .legalAddress(getAddress(updatedProfile.getLegalAddress()))
                        .businessAddress(getAddress(updatedProfile.getBusinessAddress()))
                        .taxIdentifiers(buildTaxIdentifiers(updatedProfile))
                        .build())
                .build();
    }

    public static ProfileEntity buildProfileEntity(BusinessProfile profile, ProfileEntity existingProfileEntity) {
        return ProfileEntity.builder()
                .id(existingProfileEntity.getId())
                .legalName(profile.getLegalName())
                .companyName(profile.getCompanyName())
                .email(profile.getEmail())
                .website(profile.getWebsite())
                .businessAddress(getAddress(profile.getBusinessAddress(), AddressType.BUSINESS))
                .legalAddress(getAddress(profile.getLegalAddress(), AddressType.LEGAL))
                .subscriptionEntities(getSubscriptionEntities(profile.getSubscriptionProducts().getProducts()))
                .taxIdentifiers(getTaxIdentifiers(profile.getTaxIdentifiers()))
                .build();
    }


    public static BusinessProfile buildBusinessProfile(BusinessProfileUpdateRequest request,
                                                       ProfileEntity existingProfileEntity) {
        SubscriptionProducts subscriptionProducts = buildSubscriptionProducts(existingProfileEntity);
        return BusinessProfile.builder()
                .id(existingProfileEntity.getId())
                .companyName(request.getCompanyName())
                .legalName(request.getLegalName())
                .businessAddress(request.getBusinessAddress())
                .legalAddress(request.getLegalAddress())
                .taxIdentifiers(request.getTaxIdentifiers())
                .email(request.getEmail())
                .website(request.getWebsite())
                .subscriptionProducts(subscriptionProducts)
                .build();
    }

    private static SubscriptionProducts buildSubscriptionProducts(ProfileEntity existingProfileEntity) {
        return SubscriptionProducts.builder()
                .products(existingProfileEntity.getSubscriptionEntities().stream()
                        .map(SubscriptionEntity::getProduct).collect(Collectors.toList()))
                .build();
    }

    public static BusinessProfile buildBusinessProfile(ProfileEntity profileEntity,
                                                       List<String> tobeSubscribedProducts) {
        return BusinessProfile.builder()
                .id(profileEntity.getId())
                .companyName(profileEntity.getCompanyName())
                .legalName(profileEntity.getLegalName())
                .legalAddress(getAddress(profileEntity.getLegalAddress()))
                .businessAddress(getAddress(profileEntity.getBusinessAddress()))
                .taxIdentifiers(buildTaxIdentifiers(profileEntity))
                .email(profileEntity.getEmail())
                .website(profileEntity.getWebsite())
                .subscriptionProducts(SubscriptionProducts.builder().products(tobeSubscribedProducts).build())
                .build();
    }

    public static BusinessProfile buildBusinessProfile(Long profileId, List<String> tobeSubscribedProducts,
                                                       ProfileEntity updatedEntity) {
        SubscriptionProducts subscriptionProducts = SubscriptionProducts.builder()
                .products(tobeSubscribedProducts)
                .build();
        return BusinessProfile.builder()
                .id(profileId)
                .legalName(updatedEntity.getLegalName())
                .companyName(updatedEntity.getCompanyName())
                .id(updatedEntity.getId())
                .businessAddress(getAddress(updatedEntity.getBusinessAddress()))
                .legalAddress(getAddress(updatedEntity.getLegalAddress()))
                .taxIdentifiers(buildTaxIdentifiers(updatedEntity))
                .website(updatedEntity.getWebsite())
                .email(updatedEntity.getEmail())
                .subscriptionProducts(subscriptionProducts)
                .build();
    }
}
