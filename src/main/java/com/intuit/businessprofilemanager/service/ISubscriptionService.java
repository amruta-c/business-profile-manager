package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.*;

public interface ISubscriptionService {
    SubscriptionResponse subscribe(SubscriptionRequest request);

    SubscriptionResponse subscribe(String profileId, SubscriptionProducts products);

    UnsubscriptionResponse unsubscribe(String profileId, UnsubscriptionRequest request);
}
