package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.client.ValidationClient;
import com.intuit.businessprofilemanager.exception.FutureAwaitingException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.intuit.businessprofilemanager.utils.TestConstants.*;
import static com.intuit.businessprofilemanager.utils.TestUtil.getBusinessProfile;
import static com.intuit.businessprofilemanager.utils.TestUtil.getValidationResponseResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private ValidationClient validationClient;
    @Mock
    private AppMetrics metrics;
    @InjectMocks
    private ValidationService service;

    @Test
    void testValidation() {
        List<String> products = List.of(PAYROLL, PAYMENT);
        BusinessProfile profile = getBusinessProfile();

        when(validationClient.callValidationApi(profile, PAYROLL))
                .thenReturn(getValidationResponseResponseEntity(PAYROLL, ValidationStatus.SUCCESSFUL));
        when(validationClient.callValidationApi(profile, PAYMENT))
                .thenReturn(getValidationResponseResponseEntity(PAYMENT, ValidationStatus.SUCCESSFUL));
        doNothing().when(metrics).incrementValidationApiSuccessCount();
        List<ValidationResponse> responses = service.validate(profile, products);

        responses.forEach(response -> {
            assertThat(response.getProduct()).containsAnyOf(PAYMENT, PAYROLL);
            assertEquals(ID, response.getProfileId());
            assertEquals(ValidationStatus.SUCCESSFUL, response.getStatus());
        });
        verify(metrics).incrementValidationApiSuccessCount();
        verify(validationClient).callValidationApi(profile, PAYROLL);
        verify(validationClient).callValidationApi(profile, PAYMENT);
    }

    @Test
    void testValidationWhenExceptionThrown() {
        List<String> products = List.of(PAYROLL, PAYMENT);
        BusinessProfile profile = getBusinessProfile();

        when(validationClient.callValidationApi(profile, PAYROLL))
                .thenReturn(getValidationResponseResponseEntity(PAYROLL, ValidationStatus.SUCCESSFUL));
        when(validationClient.callValidationApi(profile, PAYMENT)).thenThrow(new RuntimeException(ERROR_MESSAGE));

        FutureAwaitingException exception = assertThrows(FutureAwaitingException.class,
                () -> service.validate(profile, products),
                "Expect validate() to throw FutureAwaitingException but it didn't");

        String expectedMessage = String.format(
                "An exception occurred while trying to perform validation for profileId : %s and products : %s",
                profile.getId(), products);
        assertEquals(expectedMessage, exception.getMessage());
        verifyNoInteractions(metrics);
        verify(validationClient).callValidationApi(profile, PAYROLL);
        verify(validationClient).callValidationApi(profile, PAYMENT);
    }
}