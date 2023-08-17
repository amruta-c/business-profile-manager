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
     * Validates the specified business profile against the list of subscribed products.
     * This method checks if the provided business profile is valid for the given set of subscribed products.
     *
     * @param profile The business profile to be validated.
     * @param products The list of subscribed products for which the profile needs to be validated.
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
