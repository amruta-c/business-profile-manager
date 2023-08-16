package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.*;

public interface ISubscriptionService {
    SubscriptionResponse subscribe(SubscriptionRequest request);

    SubscriptionResponse subscribe(Long profileId, SubscriptionProducts products);

    UnsubscriptionResponse unsubscribe(Long profileId, UnsubscriptionRequest request);
}
