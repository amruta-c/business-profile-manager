package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import com.intuit.businessprofilemanager.model.BusinessProfileEntity;
import com.intuit.businessprofilemanager.service.BusinessProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profiles")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(BusinessProfileService businessProfileService) {
        this.businessProfileService = businessProfileService;
    }

    /**
     * @param profileId
     * @return
     */
    @GetMapping("/{profile_id}")
    public ResponseEntity<BusinessProfileEntity> getBusinessProfile(@PathVariable(name = "profile_id") String profileId) {
        return ResponseEntity.ok(businessProfileService.getProfile(profileId));
    }

    /**
     * @param profileId
     * @param profile
     * @return
     */
    @PutMapping("/{profile_id}")
    public ResponseEntity<BusinessProfileEntity> updateProfile(@PathVariable(name = "profile_id") String profileId,
                                                               @RequestBody @Valid BusinessProfile profile) {
        return ResponseEntity.ok(businessProfileService.updateProfile(profileId, profile));
    }

}
