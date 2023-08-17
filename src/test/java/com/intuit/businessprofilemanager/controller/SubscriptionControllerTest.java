package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.exception.ExceptionHandlerAdvice;
import com.intuit.businessprofilemanager.exception.FutureAwaitingException;
import com.intuit.businessprofilemanager.exception.ValidationApiFailureException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.service.SubscriptionService;
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

import java.util.Collections;
import java.util.List;

import static com.intuit.businessprofilemanager.utils.TestConstants.*;
import static com.intuit.businessprofilemanager.utils.TestUtil.getBusinessProfile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = SubscriptionController.class)
@AutoConfigureWebTestClient(timeout = "10000")
@Import({ExceptionHandlerAdvice.class, AppMetrics.class, SimpleMeterRegistry.class})
public class SubscriptionControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private SubscriptionService service;

    @Test
    void testSubscribe_ValidRequest() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setProfile(getBusinessProfile());
        request.setProducts(Collections.singletonList("product"));

        SubscriptionResponse response = new SubscriptionResponse(123L, "Subscribed successfully");

        when(service.subscribe(request)).thenReturn(response);
        webTestClient.post()
                .uri("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponse.class)
                .isEqualTo(response);
    }

    @Test
    void testSubscribe_WhenFutureAwaitingExceptionIsThrown() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setProfile(getBusinessProfile());
        request.setProducts(Collections.singletonList("product"));

        when(service.subscribe(request)).thenThrow(new FutureAwaitingException(ERROR_MESSAGE));
        webTestClient.post()
                .uri("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getResponseCode());
                    assertEquals("Exception caught while waiting for validation result futures.",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

    @Test
    void testSubscribe_WhenValidationApiFailureExceptionIsThrown() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setProfile(getBusinessProfile());
        request.setProducts(Collections.singletonList("product"));

        when(service.subscribe(request)).thenThrow(new ValidationApiFailureException(ERROR_MESSAGE));

        webTestClient.post()
                .uri("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, errorResponse.getResponseCode());
                    assertEquals("Data validation API failure",
                            errorResponse.getResponseMessage());
                    assertEquals(ERROR_MESSAGE, errorResponse.getResponseDetail());
                });
    }

    @Test
    void testSubscribe_WhenDataValidationExceptionIsThrown() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setProfile(getBusinessProfile());
        request.setProducts(Collections.singletonList("product"));
        List<ValidationResponse> response = List.of(ValidationResponse.builder()
                .product(PAYROLL)
                .profileId(ID)
                .status(ValidationStatus.FAILED)
                .build());
        when(service.subscribe(request)).thenThrow(new DataValidationException(response));

        webTestClient.post()
                .uri("/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    assertEquals(HttpStatus.EXPECTATION_FAILED, errorResponse.getResponseCode());
                    assertEquals("Data validation failed", errorResponse.getResponseMessage());
                    assertThat(errorResponse.getResponseDetail())
                            .contains("Failed to validate profile data for profileId : ")
                            .contains(PAYROLL);
                });
    }

    @Test
    void testUpdateSubscribe_ValidRequest() {
        SubscriptionProducts products = new SubscriptionProducts(List.of(PAYROLL));
        Long profileId = 123L;

        SubscriptionResponse response = new SubscriptionResponse(profileId, "Subscribed successfully");

        when(service.subscribe(profileId, products)).thenReturn(response);
        webTestClient.post()
                .uri("/profiles/{profile_id}/subscribe", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(products)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponse.class)
                .isEqualTo(response);
    }

}
