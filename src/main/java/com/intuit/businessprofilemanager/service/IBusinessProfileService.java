package com.intuit.businessprofilemanager.service;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.BusinessProfileData;
import com.intuit.businessprofilemanager.model.BusinessProfileUpdateRequest;

import java.util.List;

public interface IBusinessProfileService {
    Long createProfile(BusinessProfile profile, List<String> products);

    BusinessProfileData getProfile(Long profileId);

    BusinessProfileData updateProfile(Long profileId, BusinessProfileUpdateRequest profile);

    boolean deleteProfile(Long profileId);

    BusinessProfile updateSubscription(Long profileId, List<String> subscriptions);
}
