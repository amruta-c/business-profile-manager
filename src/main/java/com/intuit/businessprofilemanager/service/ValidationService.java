package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.client.ValidationClient;
import com.intuit.businessprofilemanager.exception.FutureAwaitingException;
import com.intuit.businessprofilemanager.exception.ValidationApiFailureException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ValidationService implements IValidationService {

    private final ValidationClient validationClient;
    private final AppMetrics metrics;

    public ValidationService(ValidationClient validationClient, AppMetrics metrics) {
        this.validationClient = validationClient;
        this.metrics = metrics;
    }

    @Override
    public ValidationResponse validate(BusinessProfile profile, List<String> products) {
        List<CompletableFuture<ValidationResponse>> validationFutures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> validateProduct(profile, product)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(validationFutures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(); // Wait for all validation futures to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new FutureAwaitingException();
        }

        List<ValidationResponse> validationResponses = validationFutures.stream()
                .map(this::getValidationResponse)
                .collect(Collectors.toList());

        // Aggregate and return validation responses
        return aggregateValidationResponses(validationResponses);
    }

    private ValidationResponse validateProduct(BusinessProfile profile, String product) {
        try {
            ResponseEntity<ValidationResponse> validationResponse = validationClient.callValidationApi(profile, product);
            return validationResponse.getBody();
        } catch (ValidationApiFailureException | ExecutionException ex) {
            metrics.incrementValidationApiFailureCount();
            throw new ValidationApiFailureException();
        }
    }

    private ValidationResponse getValidationResponse(CompletableFuture<ValidationResponse> future) {
        try {
            return future.get(); // Retrieve the result of each validation future
        } catch (InterruptedException | ExecutionException e) {
            return ValidationResponse.builder()
                    .status(ValidationStatus.FAILED)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .validationMessage("Exception occurred while processing: " + e.getMessage())
                    .build();
        }
    }

    private ValidationResponse aggregateValidationResponses(List<ValidationResponse> responses) {
        boolean hasFailedResponse = responses.stream()
                .anyMatch(response -> response.getStatus() == ValidationStatus.FAILED);

        HttpStatus aggregatedStatus = hasFailedResponse ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;

        if (hasFailedResponse) {
            metrics.incrementValidationApiFailureCount();
            return ValidationResponse.builder()
                    .status(ValidationStatus.FAILED)
                    .statusCode(aggregatedStatus)
                    .validationMessage("Validation failed for one or more products")
                    .build();
        } else {
            StringBuilder summaryMessage = new StringBuilder();
            for (ValidationResponse response : responses) {
                summaryMessage.append(response.getValidationMessage()).append("\n");
            }
            metrics.incrementValidationApiSuccessCount();
            return ValidationResponse.builder()
                    .status(ValidationStatus.SUCCESSFUL)
                    .statusCode(aggregatedStatus)
                    .validationMessage(summaryMessage.toString())
                    .build();
        }
    }

}
