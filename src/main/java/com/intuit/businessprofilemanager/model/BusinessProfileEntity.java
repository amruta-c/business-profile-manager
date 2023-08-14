package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessProfileEntity {
    private BusinessProfile profile;
    private List<String> subscribedProducts;
}
