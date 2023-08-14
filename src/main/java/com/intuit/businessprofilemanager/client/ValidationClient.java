package com.intuit.businessprofilemanager.client;

import com.intuit.businessprofilemanager.exception.InvalidDataException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
    @Retryable(maxAttemptsExpression = "#{${validation.api.maxAttempts:3}}", value = {HttpServerErrorException.class}, backoff = @Backoff(delayExpression = "#{${validation.api.backOffDelay:1000}}"))
    public ResponseEntity<ValidationResponse> callValidationApi(BusinessProfile request, String product) {
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

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                // Successful response (2xx)
                return ResponseEntity.ok(ValidationResponse.builder().productId(product).status(ValidationStatus.SUCCESSFUL).validationMessage(responseEntity.getBody()).build());
            } else if (statusCode.is4xxClientError()) {
                // Client-side error (4xx)
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            } else if (statusCode.is5xxServerError()) {
                // Server-side error (5xx)
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                // Handle other cases (non-2xx, non-4xx, non-5xx)
                throw new InvalidDataException();
            }
        } catch (RestClientException e) {
            return ResponseEntity.internalServerError().body(ValidationResponse.builder().status(ValidationStatus.FAILED).validationMessage(e.getMessage()).build());
        }
    }

    public ResponseEntity<ValidationResponse> fallbackValidation(BusinessProfile request, String product, Exception ex) {
        // Implement your fallback logic here
        log.error("Fallback validation performed due to service unavailability:");
        return ResponseEntity.internalServerError()
                .body(ValidationResponse.builder()
                        .status(ValidationStatus.FAILED)
                        .validationMessage("Fallback validation performed due to service unavailability: " + ex.getMessage())
                        .build());
    }
}
