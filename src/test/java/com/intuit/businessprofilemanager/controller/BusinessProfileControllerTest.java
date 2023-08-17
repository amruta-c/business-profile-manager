package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.exception.ExceptionHandlerAdvice;
import com.intuit.businessprofilemanager.model.BusinessProfileData;
import com.intuit.businessprofilemanager.service.BusinessProfileService;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {AppMetrics.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
@Import({BusinessProfileController.class, SubscriptionController.class, ExceptionHandlerAdvice.class, AppMetrics.class})
class BusinessProfileControllerTest {


    private static final String BUSINESS_PROFILE_URL = "/profiles/";

    @MockBean
    private BusinessProfileService businessProfileService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test get profile for positive test case")
    void testGetProfile() throws Exception {
        Long validProfileId = 123L;
        BusinessProfileData data = BusinessProfileData.builder().build();
        when(businessProfileService.getProfile(validProfileId)).thenReturn(data);
        var result = mockMvc.perform(get("/profiles/" + validProfileId)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK);
//        webTestClient.get()
//                .uri("/profiles/" + validProfileId)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(BusinessProfileData.class)
//                .isEqualTo(data);
//        getTo(BUSINESS_PROFILE_URL + validProfileId)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(BusinessProfileData.class);
    }

    @Test
    @DisplayName("Test get profile for invalid profileId")
    void testGetProfileForInvalidProfileId() {
        String invalidId = "";
//        getTo(BUSINESS_PROFILE_URL + invalidId)
//                .exchange()
//                .expectStatus()
//                .isBadRequest()
//                .expectBody();
    }

//    private WebTestClient.RequestBodySpec putTo(String url) {
//        return webTestClient.put().uri(url).contentType(MediaType.APPLICATION_JSON);
//    }


}