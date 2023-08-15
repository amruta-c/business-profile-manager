package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ValidationRequest {
    private String companyName;
    private String legalName;
    private Address businessAddress;
    private Address legalAddress;
    private List<TaxIdentifier> taxIdentifiers;
    private String email;
    private String website;
    private String product;
}
