package com.intuit.businessprofilemanager.exception;

import com.intuit.businessprofilemanager.model.ValidationResponse;

import java.util.List;

public class DataValidationException extends RuntimeException {
    List<ValidationResponse> failedValidationResponses;

    public DataValidationException(List<ValidationResponse> failedValidationResponses) {
        super();
        this.failedValidationResponses = failedValidationResponses;
    }

    public List<ValidationResponse> getFailedValidationResponses() {
        return failedValidationResponses;
    }
}
