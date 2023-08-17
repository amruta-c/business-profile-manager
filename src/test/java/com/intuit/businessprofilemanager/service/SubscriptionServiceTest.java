package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import com.intuit.businessprofilemanager.utils.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.businessprofilemanager.utils.TestConstants.ID;
import static com.intuit.businessprofilemanager.utils.TestConstants.PAYROLL;
import static com.intuit.businessprofilemanager.utils.TestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private IBusinessProfileService businessProfileService;
    @Mock
    private AppMetrics metrics;
    @InjectMocks
    private SubscriptionService service;

    @Test
    void testSubscribeWhenAllProductsValidationIsSuccessful() {
        BusinessProfile businessProfile = getBusinessProfile();
        List<String> products = List.of(PAYROLL);
        SubscriptionRequest request = new SubscriptionRequest(businessProfile, products);
        doNothing().when(validationUtil).validateProfileWithProducts(businessProfile, products);
        doNothing().when(metrics).incrementSubscriptionCount();
        when(businessProfileService.createProfile(businessProfile, products)).thenReturn(ID);

        SubscriptionResponse response = service.subscribe(request);

        assertEquals(ID, response.getProfileId());
        assertEquals("Business profile is validated and subscribed successfully", response.getMessage());
        verify(validationUtil).validateProfileWithProducts(businessProfile, products);
        verify(metrics).incrementSubscriptionCount();
        verify(businessProfileService).createProfile(businessProfile, products);
    }

    @Test
    void testSubscribeWhenAllProductValidationFails() {
        BusinessProfile businessProfile = getBusinessProfile();
        List<String> products = List.of(PAYROLL);
        SubscriptionRequest request = new SubscriptionRequest(businessProfile, products);
        List<ValidationResponse> validationResponses = getValidationResponses(ValidationStatus.FAILED, 2);
        doThrow(new DataValidationException(validationResponses)).when(validationUtil).validateProfileWithProducts(businessProfile, products);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> service.subscribe(request), "Expect subscribe() to throw DataValidationException but it didn't");

        assertEquals(validationResponses, exception.getFailedValidationResponses());
        verify(validationUtil).validateProfileWithProducts(businessProfile, products);
        verify(metrics, times(0)).incrementSubscriptionCount();
        verify(businessProfileService, times(0)).createProfile(businessProfile, products);
    }

    @Test
    void testUpdateSubscribe() {
        List<String> products = new ArrayList<>();
        products.add(PAYROLL);
        SubscriptionProducts subscriptionProducts = getSubscriptionProducts(products);
        BusinessProfile businessProfile = getBusinessProfile(products);
        when(businessProfileService.updateSubscription(ID, products)).thenReturn(businessProfile);

        SubscriptionResponse response = service.subscribe(ID, subscriptionProducts);

        assertEquals(ID, response.getProfileId());
        assertThat(response.getMessage())
                .contains("Subscribed to the products")
                .contains(PAYROLL);
        verify(metrics).incrementSubscriptionCount();
        verify(businessProfileService).updateSubscription(ID, products);
    }


    @Test
    void testUnSubscribe() {
        //todo complete code and add test case
    }
}