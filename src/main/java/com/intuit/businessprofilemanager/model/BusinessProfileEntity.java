package com.intuit.businessprofilemanager.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BusinessProfileEntity {
    private BusinessProfile profile;
    private List<String> subscribedProducts;
}
