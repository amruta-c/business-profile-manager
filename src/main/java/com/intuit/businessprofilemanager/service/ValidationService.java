package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.client.ValidationClient;
import com.intuit.businessprofilemanager.exception.FutureAwaitingException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ValidationService implements IValidationService {

    private final ValidationClient validationClient;
    private final AppMetrics metrics;

    public ValidationService(ValidationClient validationClient, AppMetrics metrics) {
        this.validationClient = validationClient;
        this.metrics = metrics;
    }

    @Override
    public List<ValidationResponse> validate(BusinessProfile profile, List<String> products) {
        List<CompletableFuture<ValidationResponse>> validationFutures = products.stream()
                .map(product -> CompletableFuture.supplyAsync(() -> validateProduct(profile, product)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                validationFutures.toArray(new CompletableFuture[products.size()])
        );
        CompletableFuture<List<ValidationResponse>> futures = allFutures.thenApply(v ->
                validationFutures.stream().map(CompletableFuture::join).collect(Collectors.toList())); // Wait
        List<ValidationResponse> responses;
        try {
            responses = futures.get();
            metrics.incrementValidationApiSuccessCount();
        } catch (InterruptedException | ExecutionException e) {
            String message = String.format(
                    "An exception occurred while trying to perform validation for profileId : %s and products : %s",
                    profile.getId(), products);
            log.error(message, e);
            throw new FutureAwaitingException(message);
        }
        return responses;
    }

    private ValidationResponse validateProduct(BusinessProfile profile, String product) {
        ResponseEntity<ValidationResponse> validationResponse = validationClient.callValidationApi(profile, product);
        return validationResponse.getBody();
    }

}
