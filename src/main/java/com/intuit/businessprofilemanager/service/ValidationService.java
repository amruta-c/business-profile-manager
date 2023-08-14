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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ValidationService implements IValidationService {

    private final ValidationClient validationClient;

    public ValidationService(ValidationClient validationClient) {
        this.validationClient = validationClient;
    }

    @Override
    public ValidationResponse validate(BusinessProfile profile, List<String> products) {
        List<CompletableFuture<ValidationResponse>> validationFutures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> {
                    try {
                        ResponseEntity<ValidationResponse> validationResponse = validationClient.callValidationApi(profile, product);
                        return validationResponse.getBody();
                    } catch (HttpClientErrorException | HttpServerErrorException | InvalidDataException ex) {
                        return ValidationResponse.builder()
                                .status(ValidationStatus.FAILED)
                                .validationMessage(ex.getMessage())
                                .build();
                    }
                }))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(validationFutures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(); // Wait for all validation futures to complete
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions that might occur while waiting for futures
            e.printStackTrace();
        }

        List<ValidationResponse> validationResponses = validationFutures.stream()
                .map(future -> {
                    try {
                        return future.get(); // Retrieve the result of each validation future
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return ValidationResponse.builder()
                                .status(ValidationStatus.FAILED)
                                .validationMessage("Exception occurred while processing" + e.getMessage())
                                .build();
                    }
                })
                .collect(Collectors.toList());

        // Aggregate validation responses
        return aggregateValidationResponses(validationResponses);
    }

    private ValidationResponse aggregateValidationResponses(List<ValidationResponse> responses) {
        boolean hasFailedResponse = responses.stream()
                .anyMatch(response -> response.getStatus() == ValidationStatus.FAILED);

        if (hasFailedResponse) {
            // If any response has a "FAILED" status, return a combined response with "FAILED" status
            return ValidationResponse.builder()
                    .status(ValidationStatus.FAILED)
                    .validationMessage("Validation failed for one or more products")
                    .build();
        } else {
            // Combine the responses using your own aggregation logic
            // For example, you can create a summary message from successful responses
            StringBuilder summaryMessage = new StringBuilder();
            for (ValidationResponse response : responses) {
                summaryMessage.append(response.getValidationMessage()).append("\n");
            }

            return ValidationResponse.builder()
                    .status(ValidationStatus.SUCCESSFUL)
                    .validationMessage(summaryMessage.toString())
                    .build();
        }
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
