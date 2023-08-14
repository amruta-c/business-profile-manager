package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SubscriptionService implements ISubscriptionService {

    private final IValidationService validationService;
    private final IBusinessProfileService businessProfileService;

    public SubscriptionService(IValidationService validationService, IBusinessProfileService businessProfileService) {
        this.validationService = validationService;
        this.businessProfileService = businessProfileService;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        ValidationResponse validationResponse = validationService.validate(request.getProfile(), request.getProducts());
        if (validationResponse.getStatus() == ValidationStatus.FAILED) {
            //build and return response;
            return new SubscriptionResponse(null, "", ErrorResponse.builder().build());
        }

        String profileId = businessProfileService.createProfile(request.getProfile(), request.getProducts());
        return new SubscriptionResponse(profileId, "Business profile is updated and subscribed to products", null);
    }

    /**
     * @param profileId
     * @param subscriptionsRequested
     * @return
     */

    @Override
    public SubscriptionResponse subscribe(String profileId, SubscriptionProducts subscriptionsRequested) {
        BusinessProfileEntity profileEntity = businessProfileService.getProfile(profileId);
        List<String> alreadySubscribed = profileEntity.getSubscribedProducts();
        List<String> allSubscriptions = new ArrayList<>(new HashSet<>(Stream.concat(alreadySubscribed.stream(),
                subscriptionsRequested.getProducts().stream()).collect(Collectors.toList())));

        if (allSubscriptions.size() == subscriptionsRequested.getProducts().size()) {
            return new SubscriptionResponse(profileEntity.getProfile().getId(), "Already subscribed to products", null);
        }
        businessProfileService.updateSubscription(profileId, allSubscriptions);
        return new SubscriptionResponse(profileId, "Subscribed to the products", null);
    }

    /**
     * @param request
     * @return
     */
    @Override
    public UnsubscriptionResponse unsubscribe(String profileId, UnsubscriptionRequest request) {
        //unlink and mark profile as inactive
        businessProfileService.deleteProfile(profileId);
        return null;
    }
}
