package com.intuit.businessprofilemanager.utils;

import com.intuit.businessprofilemanager.entity.AddressEntity;
import com.intuit.businessprofilemanager.entity.ProfileEntity;
import com.intuit.businessprofilemanager.entity.SubscriptionEntity;
import com.intuit.businessprofilemanager.entity.TaxIdentifiersEntity;
import com.intuit.businessprofilemanager.model.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.intuit.businessprofilemanager.utils.TestConstants.ID;

public class TestUtil {


    public static BusinessProfile getBusinessProfile() {
        return BusinessProfile.builder()
                .email("test@email.com")
                .legalName("testLegalName")
                .companyName("testCompany")
                .website("http://www.test.com")
                .taxIdentifiers(List.of(TaxIdentifier.builder()
                        .taxIdentifierNo("123")
                        .taxIdentifierType(TaxIdentifierType.EAN)
                        .build()))
                .legalAddress(Address.builder()
                        .line1("line1")
                        .city("Bengaluru")
                        .state("KA")
                        .build())
                .businessAddress(Address.builder()
                        .line1("line1")
                        .city("Bengaluru")
                        .state("KA")
                        .build())
                .build();
    }

    public static BusinessProfile getBusinessProfile(List<String> products) {
        BusinessProfile profile = getBusinessProfile();
        profile.setSubscriptionProducts(getSubscriptionProducts(products));
        return profile;
    }


    public static ResponseEntity<ValidationResponse> getValidationResponseResponseEntity(String product,
                                                                                         ValidationStatus status) {
        return ResponseEntity.ok(ValidationResponse.builder()
                .product(product)
                .profileId(ID)
                .status(status)
                .build());
    }

    public static List<ValidationResponse> getValidationResponses(ValidationStatus status, int num) {
        List<ValidationResponse> responses = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            responses.add(ValidationResponse.builder()
                    .status(status)
                    .build());
        }
        return responses;
    }

    public static ProfileEntity getProfileEntityWithSubscriptions(long profileId, String product) {
        return ProfileEntity.builder()
                .id(profileId)
                .businessAddress(AddressEntity.builder().build())
                .legalAddress(AddressEntity.builder().build())
                .taxIdentifiers(Set.of(TaxIdentifiersEntity.builder().build()))
                .subscriptionEntities(Set.of(SubscriptionEntity.builder().product(product).build()))
                .build();
    }

    public static SubscriptionProducts getSubscriptionProducts(List<String> products) {
        return SubscriptionProducts.builder().products(products).build();
    }

    public static BusinessProfileUpdateRequest buildBusinessProfileUpdateRequest(String email, String companyName) {
        return BusinessProfileUpdateRequest.builder()
                .companyName(companyName)
                .legalName("Dummy Legal")
                .businessAddress(buildDummyAddress())
                .legalAddress(buildDummyAddress())
                .taxIdentifiers(Collections.singletonList(buildDummyTaxIdentifier()))
                .email(email)
                .website("http://www.example.com")
                .build();
    }

    private static Address buildDummyAddress() {
        return Address.builder()
                .line1("123 Main St")
                .line2("124 st")
                .city("Dummy City")
                .state("Dummy State")
                .zip("12345")
                .country("Dummy Country")
                .build();
    }

    private static TaxIdentifier buildDummyTaxIdentifier() {
        return TaxIdentifier.builder()
                .taxIdentifierType(TaxIdentifierType.EAN)
                .taxIdentifierNo("123456")
                .build();
    }
}
