package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.exception.DataValidationException;
import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.utils.AppMetrics;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubscriptionService implements ISubscriptionService {

    private final IValidationService validationService;
    private final IBusinessProfileService businessProfileService;
    private final AppMetrics metrics;

    public SubscriptionService(IValidationService validationService, IBusinessProfileService businessProfileService, AppMetrics metrics) {
        this.validationService = validationService;
        this.businessProfileService = businessProfileService;
        this.metrics = metrics;
    }

    /**
     * Validates and processes the subscription request, returning the subscribed profileId along with an appropriate message.
     *
     * @param request The subscription request that needs to be validated and processed for subscription.
     * @return The subscribed profileId along with a message relevant to the subscription status.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.subscribe.timer")
    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        List<ValidationResponse> validationResponses = validationService.validate(request.getProfile(),
                request.getProducts());
        List<ValidationResponse> validationFailedProducts = validationResponses.stream()
                .filter(validationResponse -> validationResponse.getStatus() == ValidationStatus.FAILED)
                .collect(Collectors.toList());
        if (!validationFailedProducts.isEmpty()) {
            throw new DataValidationException(validationFailedProducts);
        }

        Long profileId = businessProfileService.createProfile(request.getProfile(), request.getProducts());
        log.info("Business profile with profileId: {} has been subscribed successfully", profileId);
        metrics.incrementSubscriptionCount();
        return new SubscriptionResponse(profileId, "Business profile is validated and subscribed successfully");
    }

    /**
     * Subscribes to the requested products for the given profileId and provides a message indicating the subscription status.
     *
     * @param profileId              The unique identifier for each subscription.
     * @param subscriptionsRequested The list of products in the subscription request that need validation and processing.
     * @return The subscribed profileId along with a pertinent message about the subscription outcome.
     */

    @Override
    @Timed(value = "business-profile-manager.endpoint.subscribe-products.timer")
    public SubscriptionResponse subscribe(Long profileId, SubscriptionProducts subscriptionsRequested) {
        List<String> tobeSubscribedProducts = subscriptionsRequested.getProducts();
        BusinessProfile updatedProfile = businessProfileService.updateSubscription(profileId, tobeSubscribedProducts);
        log.info("ProfileId: {} has been subscribed to the products: {} successfully", profileId, subscriptionsRequested.getProducts().toString());
        metrics.incrementSubscriptionCount();
        return new SubscriptionResponse(profileId, "Subscribed to the products: " + updatedProfile.getSubscriptionProducts().getProducts());
    }

    /**
     * Processes the unsubscription request for the given profileId and products, returning the unsubscribed profileId along with a relevant message.
     *
     * @param profileId The unique identifier for each subscription.
     * @param request   The list of products in the unsubscription request that need to be unsubscribed.
     * @return The unsubscribed profileId along with a message relevant to the unsubscription status.
     */
    @Override
    @Timed(value = "business-profile-manager.endpoint.unsubscribe.timer")
    public UnsubscriptionResponse unsubscribe(Long profileId, UnsubscriptionRequest request) {

        // todo not in requirement,
        /**
         *  sub repo .getall sub
         *  sub repo .delet
         *  if empty sub then mark it as inactive.
         *  add active in create and update and subscribe()
         */
        boolean isUnsubscribeSuccessful = businessProfileService.deleteProfile(profileId);
        if (isUnsubscribeSuccessful) {
            log.info("ProfileId: {} has been successfully unsubscribed.", profileId);
            metrics.incrementUnsubscriptionCount();
            return UnsubscriptionResponse.builder()
                    .profileId(profileId)
                    .message("The business profile has been successfully unsubscribed.")
                    .build();
        }
        log.error("ProfileId: {} couldn't get unsubscribed due to some issue", profileId);
        return UnsubscriptionResponse.builder()
                .profileId(profileId)
                .message("The business profile couldn't get unsubscribed.")
                .build();
    }
}
