package com.intuit.businessprofilemanager.exception;

public class ValidationApiFailureException extends RuntimeException {
    public ValidationApiFailureException(String message) {
        super(message);
    }
}
