package com.intuit.businessprofilemanager.utils;

import com.intuit.businessprofilemanager.entity.AddressEntity;
import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.entity.TaxIdentifiersEntity;
import com.intuit.businessprofilemanager.model.Address;
import com.intuit.businessprofilemanager.model.AddressType;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.TaxIdentifier;
import lombok.extern.slf4j.Slf4j;

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
                .subscriptionEntities(getSubscriptionEntities(profile.getSubscriptionProducts().get(0).getProducts()))
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
}
