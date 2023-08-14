package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.client.ValidationClient;
import com.intuit.businessprofilemanager.exception.InvalidDataException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
public class ValidationService implements IValidationService {

    private final ValidationClient validationClient;

    public ValidationService(ValidationClient validationClient) {
        this.validationClient = validationClient;
    }

    @Override
    public ValidationResponse validate(BusinessProfile profile, List<String> product) {
        return null;
    }

    /**
     * TODO: update all description
     *
     * @param profile
     * @param product
     * @return
     */
    @Override
    public ValidationResponse validate(BusinessProfile profile, String product) {

        // parallel validation
        //retries
        //timeout
        //ckt breaker
        /**
         * Handle HTTP client errors (4xx) or other exceptions that might occur
         * This could include network errors, parsing errors, etc.
         */
        try {
            ResponseEntity<ValidationResponse> validationResponse = validationClient.callValidationApi(profile, product);
            return validationResponse.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException | InvalidDataException ex) {
            return ValidationResponse.builder().status(ValidationStatus.FAILED).validationMessage(ex.getMessage()).build();
        }
    }
}
