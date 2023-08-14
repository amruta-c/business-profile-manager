package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionResponse {
    private String profileId;
    private String message;
    private ErrorResponse errorResponse;
}
