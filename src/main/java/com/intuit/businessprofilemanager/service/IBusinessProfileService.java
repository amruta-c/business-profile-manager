package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.BusinessProfileEntity;

import java.util.List;

public interface IBusinessProfileService {
    String createProfile(BusinessProfile profile, List<String> products);

    BusinessProfileEntity getProfile(String profileId);

    BusinessProfileEntity updateProfile(String profileId, BusinessProfile profile);

    boolean deleteProfile(String profileId);

    BusinessProfile updateSubscription(String profileId, List<String> subscriptions);
}
