package com.intuit.businessprofilemanager.utils;

import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.ValidationResponse;
import com.intuit.businessprofilemanager.model.ValidationStatus;
import com.intuit.businessprofilemanager.service.IValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.intuit.businessprofilemanager.utils.TestConstants.PAYROLL;
import static com.intuit.businessprofilemanager.utils.TestUtil.getBusinessProfile;
import static com.intuit.businessprofilemanager.utils.TestUtil.getValidationResponses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {

    @Mock
    private IValidationService validationService;

    @InjectMocks
    private ValidationUtil validationUtil;

    @Test
    void testValidateProfileWithProductsWhenValidationIsSuccessful() {
        BusinessProfile businessProfile = getBusinessProfile();
        List<String> products = List.of(PAYROLL);
        when(validationService.validate(businessProfile, products))
                .thenReturn(getValidationResponses(ValidationStatus.SUCCESSFUL, 1));

        validationUtil.validateProfileWithProducts(businessProfile, products);

        verify(validationService).validate(businessProfile, products);
    }

    @Test
    void testValidateProfileWithProductsWhenValidationFails() {
        BusinessProfile businessProfile = getBusinessProfile();
        List<String> products = List.of(PAYROLL);
        List<ValidationResponse> validationResponses = getValidationResponses(ValidationStatus.FAILED, 1);
        when(validationService.validate(businessProfile, products))
                .thenReturn(validationResponses);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validationUtil.validateProfileWithProducts(businessProfile, products),
                "Expect validateProfileWithProducts() to throw DataValidationException but it didn't");

        assertEquals(validationResponses, exception.getFailedValidationResponses());
        verify(validationService).validate(businessProfile, products);
    }

}