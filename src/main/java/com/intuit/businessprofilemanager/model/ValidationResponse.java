package com.intuit.businessprofilemanager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResponse {
    private String productId;
    private ValidationStatus status;
    private String validationMessage;
}
