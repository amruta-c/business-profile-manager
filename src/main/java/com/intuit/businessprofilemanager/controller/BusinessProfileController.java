package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.BusinessProfileEntity;
import com.intuit.businessprofilemanager.service.BusinessProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profiles")
@Slf4j
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(BusinessProfileService businessProfileService) {
        this.businessProfileService = businessProfileService;
    }

    /**
     * Retrieves the business profile details associated with the given profileId that is subscribed.
     *
     * @param profileId The unique identifier for each subscription.
     * @return Business profile details corresponding to the subscribed profile id.
     */
    @GetMapping("/{profile_id}")
    public ResponseEntity<BusinessProfileEntity> getBusinessProfile(@PathVariable(name = "profile_id") String profileId) {
        log.info("Profile details has been fetched successfully for profileId: {}", profileId);
        return ResponseEntity.ok(businessProfileService.getProfile(profileId));
    }

    /**
     * Updates the business profile associated with the given profileId and provides details about the outcome of the profile update.
     *
     * @param profileId The unique identifier for each subscription.
     * @param profile   The business profile that needs to be updated.
     * @return Business profile details reflecting the result of the profile update.
     */
    @PutMapping("/{profile_id}")
    public ResponseEntity<BusinessProfileEntity> updateProfile(@PathVariable(name = "profile_id") String profileId,
                                                               @RequestBody @Valid BusinessProfile profile) {
        log.info("Profile has been updated successfully for profileId: {}", profileId);
        return ResponseEntity.ok(businessProfileService.updateProfile(profileId, profile));
    }

}
