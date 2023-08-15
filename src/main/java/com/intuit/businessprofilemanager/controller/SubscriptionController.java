package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.*;
import com.intuit.businessprofilemanager.service.ISubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SubscriptionController {

    private final ISubscriptionService subscriptionService;

    public SubscriptionController(ISubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody @Valid SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.subscribe(request));
    }

    /**
     * @param profileId
     * @param products
     * @return
     */
    @PostMapping("/profiles/{profile_id}/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@PathVariable(name = "profile_id") String profileId,
                                                          @RequestBody @Valid SubscriptionProducts products) {
        return ResponseEntity.ok(subscriptionService.subscribe(profileId, products));
    }

    /**
     * @param profileId
     * @param request
     * @return
     */
    @PostMapping("/profiles/{profile_id}/unsubscribe")
    public ResponseEntity<UnsubscriptionResponse> unsubscribe(@PathVariable(name = "profile_id") String profileId,
                                                              @RequestBody @Valid UnsubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.unsubscribe(profileId, request));
    }

}
