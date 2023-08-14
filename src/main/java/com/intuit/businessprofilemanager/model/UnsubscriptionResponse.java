package com.intuit.businessprofilemanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnsubscriptionResponse {
    private String profileId;
}
