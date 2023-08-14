package com.intuit.businessprofilemanager.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
