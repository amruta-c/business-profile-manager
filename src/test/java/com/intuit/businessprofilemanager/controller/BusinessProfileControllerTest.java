package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.exception.DataNotFoundException;
import com.intuit.businessprofilemanager.exception.ExceptionHandlerAdvice;
import com.intuit.businessprofilemanager.exception.RepositoryException;
import com.intuit.businessprofilemanager.model.BusinessProfileData;
import com.intuit.businessprofilemanager.model.BusinessProfileUpdateRequest;
import com.intuit.businessprofilemanager.model.ErrorResponse;
import com.intuit.businessprofilemanager.service.BusinessProfileService;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.intuit.businessprofilemanager.utils.TestConstants.ERROR_MESSAGE;
import static com.intuit.businessprofilemanager.utils.TestConstants.PAYMENT;
import static com.intuit.businessprofilemanager.utils.TestUtil.buildBusinessProfileUpdateRequest;
import static com.intuit.businessprofilemanager.utils.TestUtil.getBusinessProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BusinessProfileController.class)
@AutoConfigureWebTestClient(timeout = "1000")
@Import({
        ExceptionHandlerAdvice.class,
        AppMetrics.class,
        SimpleMeterRegistry.class
})
class BusinessProfileControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BusinessProfileService businessProfileService;

    @Test
    void testGetBusinessProfile_ValidProfileId_ReturnsProfile() {
        Long profileId = 1L;
        BusinessProfileData mockProfileData = BusinessProfileData.builder()
                .profile(getBusinessProfile()).build();
        when(businessProfileService.getProfile(profileId)).thenReturn(mockProfileData);

        webTestClient.get()
                .uri("/profiles/{profile_id}", profileId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BusinessProfileData.class)
                .isEqualTo(mockProfileData);
    }

    @Test
    void testGetBusinessProfile_InvalidProfileId() {
        Long invalidProfileId = 999L;
        when(businessProfileService.getProfile(invalidProfileId)).thenThrow(new DataNotFoundException(ERROR_MESSAGE));

        webTestClient.get()
                .uri("/profiles/{profile_id}", invalidProfileId)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.NOT_FOUND, errorResponse.getResponseCode());
                    assertEquals("Provided profileId is not subscribed or is invalid.",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

    @Test
    void testUpdateProfile_ValidData_ReturnsUpdatedProfile() {
        Long profileId = 1L;
        String email = "test@test.com";
        String companyName = "testCompany";
        BusinessProfileUpdateRequest updateRequest = buildBusinessProfileUpdateRequest(email, companyName);
        BusinessProfileData mockUpdatedProfileData = BusinessProfileData.builder()
                .profile(getBusinessProfile())
                .subscribedProducts(List.of(PAYMENT))
                .build();
        when(businessProfileService.updateProfile(profileId, updateRequest)).thenReturn(mockUpdatedProfileData);

        webTestClient.put()
                .uri("/profiles/{profile_id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BusinessProfileData.class)
                .isEqualTo(mockUpdatedProfileData);
    }

    @Test
    void testUpdateProfile_InvalidProfileId() {
        Long profileId = 1L;
        String email = "test@test.com";
        String companyName = "testCompany";
        BusinessProfileUpdateRequest updateRequest = buildBusinessProfileUpdateRequest(email, companyName);
        when(businessProfileService.updateProfile(profileId, updateRequest)).thenThrow(new DataNotFoundException(ERROR_MESSAGE));

        webTestClient.put()
                .uri("/profiles/{profile_id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.NOT_FOUND, errorResponse.getResponseCode());
                    assertEquals("Provided profileId is not subscribed or is invalid.",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

    @Test
    void testUpdateProfile_WhenRepositoryExceptionIsThrown() {
        Long profileId = 1L;
        String email = "test@test.com";
        String companyName = "testCompany";
        BusinessProfileUpdateRequest updateRequest = buildBusinessProfileUpdateRequest(email, companyName);
        when(businessProfileService.updateProfile(profileId, updateRequest)).thenThrow(new RepositoryException(ERROR_MESSAGE));

        webTestClient.put()
                .uri("/profiles/{profile_id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, errorResponse.getResponseCode());
                    assertEquals("Exception occurred in repository.",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

    @Test
    void testUpdateProfile_WhenExceptionIsThrown() {
        Long profileId = 1L;
        String email = "test@test.com";
        String companyName = "testCompany";
        BusinessProfileUpdateRequest updateRequest = buildBusinessProfileUpdateRequest(email, companyName);
        when(businessProfileService.updateProfile(profileId, updateRequest)).thenThrow(new RuntimeException(ERROR_MESSAGE));

        webTestClient.put()
                .uri("/profiles/{profile_id}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getResponseCode());
                    assertEquals("An internal server error has occurred.",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

}