package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationService implements IValidationService {
    /**
     * TODO: update all description
     * @param profile
     * @param product
     * @return
     */
    @Override
    public ValidationResponse validate(BusinessProfile profile, List<String> product) {
        // parallel validation
        //retries
        //timeout
        //ckt breaker
        return ValidationResponse.builder().status(ValidationStatus.SUCCESSFUL).build();
    }
}
