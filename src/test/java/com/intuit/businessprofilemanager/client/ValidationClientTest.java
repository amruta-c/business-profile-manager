package com.intuit.businessprofilemanager.client;

import com.intuit.businessprofilemanager.exception.ValidationApiFailureException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationRequest;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.intuit.businessprofilemanager.utils.TestConstants.*;
import static com.intuit.businessprofilemanager.utils.TestUtil.getBusinessProfile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ValidationClient validationClient;

    @Test
    void testValidationApi() {
        BusinessProfile profile = getBusinessProfile();
        ValidationResponse validationResponse = ValidationResponse.builder().product(PAYROLL).profileId(ID)
                .status(ValidationStatus.SUCCESSFUL).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ValidationRequest requestDTO = ValidationRequest.builder()
                .businessProfile(profile)
                .product(PAYROLL)
                .build();

        HttpEntity<ValidationRequest> requestEntity = new HttpEntity<>(requestDTO, headers);
        when(restTemplate.postForEntity(anyString(), eq(requestEntity), any()))
                .thenReturn(ResponseEntity.ok(validationResponse));

        ResponseEntity<ValidationResponse> responseEntity = validationClient.callValidationApi(profile, PAYROLL);
        ValidationResponse response = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(validationResponse, response);
        verify(restTemplate).postForEntity(anyString(), eq(requestEntity), any());
    }

    @Test
    void testValidationApiWhenRestClientExceptionIsThrown() {
        BusinessProfile profile = getBusinessProfile();
        profile.setId(ID);
        String apiTitleUrl = "http://localhost:9090";
        String url = apiTitleUrl + "/validate";
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                .thenThrow(new RestClientException(ERROR_MESSAGE));

        ValidationApiFailureException exception =
                assertThrows(ValidationApiFailureException.class, () -> validationClient.callValidationApi(profile,
                        PAYROLL), "Expect callValidationApi() to throw ValidationApiFailureException but it didn't");
        assertThat(exception.getMessage())
                .contains("Validation API encountered an issue")
                .contains(PAYROLL)
                .contains(String.valueOf(ID));

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), any());
    }

    @Test
    void testValidationApiFallbackValidation() {
        ResponseEntity<ValidationResponse> responseEntity = validationClient.fallbackValidation(new Throwable());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(ValidationStatus.FAILED, responseEntity.getBody().getStatus());
        assertThat(responseEntity.getBody().getValidationMessage())
                .contains("Fallback validation performed due to service unavailability");
    }

}