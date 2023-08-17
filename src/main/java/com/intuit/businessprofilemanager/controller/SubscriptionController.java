package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.SubscriptionProducts;
import com.intuit.businessprofilemanager.model.SubscriptionRequest;
import com.intuit.businessprofilemanager.model.SubscriptionResponse;
import com.intuit.businessprofilemanager.service.ISubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class SubscriptionController {

    private final ISubscriptionService subscriptionService;

    public SubscriptionController(ISubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Validates and processes the subscription request for subscription, returning a ResponseEntity containing
     * the subscribed profileId along with a message relevant to the subscription status.
     *
     * @param request The subscription request that needs to be validated and processed for subscription.
     * @return ResponseEntity containing the subscribed profileId along with a message relevant to the subscription status.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody @Valid SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.subscribe(request));
    }

    /**
     * Subscribes to the provided list of products for the given profileId and returns a ResponseEntity containing
     * the subscribed profileId along with a relevant message regarding the subscription outcome.
     *
     * @param profileId The unique identifier for each subscription.
     * @param products  The list of products in the subscription that require validation and processing.
     * @return ResponseEntity containing the subscribed profileId along with a message pertinent to the subscription outcome.
     */
    @PostMapping("/profiles/{profile_id}/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@PathVariable(name = "profile_id") Long profileId,
                                                          @RequestBody @Valid SubscriptionProducts products) {
        return ResponseEntity.ok(subscriptionService.subscribe(profileId, products));
    }

    /**
     * Processes the unsubscription request for the given profileId and products, and returns a ResponseEntity containing
     * the unsubscribed profileId along with a message relevant to the unsubscription status.
     *
     * @param profileId The unique identifier for each subscription.
     * @param request   The list of products in the unsubscription request that need to be unsubscribed.
     * @return ResponseEntity containing the unsubscribed profileId along with a message relevant to the unsubscription status.
     */
    /*@PostMapping("/profiles/{profile_id}/unsubscribe")
    public ResponseEntity<UnsubscriptionResponse> unsubscribe(@PathVariable(name = "profile_id") Long profileId,
                                                              @RequestBody @Valid UnsubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.unsubscribe(profileId, request));
    }*/

}
