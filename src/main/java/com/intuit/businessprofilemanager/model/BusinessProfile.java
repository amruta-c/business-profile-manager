package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BusinessProfile {

    @NotNull
    private Long id;
    @NotBlank
    @Size(min = 5, max = 100)
    private String companyName;
    @NotBlank
    @Size(min = 5, max = 100)
    private String legalName;
    @NotNull
    private Address businessAddress;
    @NotNull
    private Address legalAddress;
    @NotNull
    @Size(min = 1, max = 5)
    private List<TaxIdentifier> taxIdentifiers;
    @Email
    private String email;
    @Pattern(regexp = "^(http:\\/\\/|https:\\/\\/)?(www\\.)?([a-zA-Z0-9]+)\\.[a-zA-Z0-9]*\\.[a-z]{3}\\.?([a-z]+)?$")
    private String website;
    @NotNull
    private SubscriptionProducts subscriptionProducts;
}
