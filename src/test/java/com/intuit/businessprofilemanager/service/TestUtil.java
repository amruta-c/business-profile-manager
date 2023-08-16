package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.*;

import java.util.ArrayList;
import java.util.List;

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

    public static List<ValidationResponse> getValidationResponses(ValidationStatus status, int num) {
        List<ValidationResponse> responses = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            responses.add(ValidationResponse.builder()
                    .status(status)
                    .build());
        }
        return responses;
    }
}
