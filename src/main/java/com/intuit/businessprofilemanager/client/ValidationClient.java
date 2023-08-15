package com.intuit.businessprofilemanager.client;

import com.intuit.businessprofilemanager.exception.ValidationApiFailureException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ValidationClient {
    private final RestTemplate restTemplate;

    @Value("${validation.api.title.url}")
    private String apiTitleUrl;

    public ValidationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "validationCircuit", fallbackMethod = "fallbackValidation")
    @Retry(name = "validationRetry")
    public ResponseEntity<ValidationResponse> callValidationApi(BusinessProfile request, String product) throws ExecutionException {
        String url = apiTitleUrl + "/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BusinessProfile requestDTO = BusinessProfile.builder()
                .legalName(request.getLegalName())
                .companyName(request.getCompanyName())
                .legalAddress(request.getLegalAddress())
                .businessAddress(request.getBusinessAddress())
                .email(request.getEmail())
                .website(request.getWebsite())
                .taxIdentifiers(request.getTaxIdentifiers())
                .product(product)
                .build();

        HttpEntity<BusinessProfile> requestEntity = new HttpEntity<>(requestDTO, headers);

        try {
            log.info("Making request to validation API: {} at: {}", url, LocalDateTime.now());
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            return createResponseEntity(responseEntity.getStatusCode(), responseEntity.getBody());
        } catch (ValidationApiFailureException e) {
            throw new ValidationApiFailureException();
        }
    }

    public ResponseEntity<ValidationResponse> fallbackValidation(Throwable ex) {
        log.error("Fallback validation performed due to service unavailability: {}", ex.getMessage());
        return createErrorResponse("Fallback validation performed due to service unavailability: " + ex.getMessage());
    }

    private ResponseEntity<ValidationResponse> createResponseEntity(HttpStatus status, String validationMessage) {
        return ResponseEntity.status(status)
                .body(ValidationResponse.builder()
                        .status(status.is2xxSuccessful() ? ValidationStatus.SUCCESSFUL : ValidationStatus.FAILED)
                        .statusCode(status)
                        .validationMessage(validationMessage)
                        .build());
    }

    private ResponseEntity<ValidationResponse> createErrorResponse(String errorMessage) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ValidationResponse.builder()
                        .status(ValidationStatus.FAILED)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                        .validationMessage(errorMessage)
                        .build());
    }
}
