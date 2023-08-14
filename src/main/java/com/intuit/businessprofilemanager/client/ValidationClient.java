package com.intuit.businessprofilemanager.client;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ValidationClient {
    private final RestTemplate restTemplate;

    @Value("${api.title.url}")
    private String apiTitleUrl;

    public ValidationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return ResponseEntity.ok(ValidationResponse.builder().productId(product).status(ValidationStatus.SUCCESSFUL).validationMessage(responseEntity.getBody()).build());
    }
}
