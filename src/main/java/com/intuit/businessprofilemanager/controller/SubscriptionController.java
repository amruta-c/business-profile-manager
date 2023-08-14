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

    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody @Valid SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.subscribe(request));
    }

    @PostMapping("/profile/{profileId}/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody @Valid SubscriptionProducts products,
                                                          @PathVariable String profileId) {
        return ResponseEntity.ok(subscriptionService.subscribe(profileId, products));
    }

    @PostMapping("/profile/{profileId}/unsubscribe")
    public ResponseEntity<UnsubscriptionResponse> unsubscribe(@RequestBody @Valid UnsubscriptionRequest request,
                                                              @PathVariable String profileId) {
        return ResponseEntity.ok(subscriptionService.unsubscribe(profileId, request));
    }

}
