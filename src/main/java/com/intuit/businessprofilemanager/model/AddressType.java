package com.intuit.businessprofilemanager.model;

public enum AddressType {
    LEGAL("legalAddress"),
    BUSINESS("businessAddress");

    private final String value;

    AddressType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
