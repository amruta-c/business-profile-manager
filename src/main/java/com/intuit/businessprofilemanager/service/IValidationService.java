package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;

import java.util.List;

public interface IValidationService {
    ValidationResponse validate(BusinessProfile profile, List<String> products);
}
