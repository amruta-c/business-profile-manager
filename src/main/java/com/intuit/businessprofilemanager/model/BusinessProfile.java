package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BusinessProfile {

    private String id;
    private String companyName;
    private String legalName;
    private Address businessAddress;
    private Address legalAddress;
    private List<TaxIdentifier> taxIdentifiers;
    private String email;
    private String website;
    private List<SubscriptionProducts> subscriptionProducts;
    private String product;
}
