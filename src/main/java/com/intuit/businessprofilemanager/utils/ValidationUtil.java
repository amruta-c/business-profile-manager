package com.intuit.businessprofilemanager.utils;

import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import com.intuit.businessprofilemanager.service.IValidationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationUtil {
    private final IValidationService validationService;

    public ValidationUtil(IValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * todo update descr
     *
     * @param profile
     * @param products
     */
    public void validateProfileWithProducts(BusinessProfile profile, List<String> products) {
        List<ValidationResponse> responses = validationService.validate(profile, products);
        List<ValidationResponse> validationFailedResponses = responses.stream()
                .filter(response -> response.getStatus() == ValidationStatus.FAILED)
                .collect(Collectors.toList());
        if (!validationFailedResponses.isEmpty()) {
            throw new DataValidationException(validationFailedResponses);
        }
    }
}
