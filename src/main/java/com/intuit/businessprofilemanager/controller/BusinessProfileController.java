package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.BusinessProfileData;
import com.intuit.businessprofilemanager.model.BusinessProfileUpdateRequest;
import com.intuit.businessprofilemanager.service.BusinessProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
    public ResponseEntity<BusinessProfileData> getBusinessProfile(
            @PathVariable(name = "profile_id") @NotBlank Long profileId) {
        return ResponseEntity.ok(businessProfileService.getProfile(profileId));
    }

    /**
     * Updates the business profile associated with the given profileId and provides details about the outcome of the
     * profile update.
     *
     * @param profileId The unique identifier for each subscription.
     * @param request   The business profile that needs to be updated.
     * @return Business profile details reflecting the result of the profile update.
     */
    @PutMapping("/{profile_id}")
    public ResponseEntity<BusinessProfileData> updateProfile(@PathVariable(name = "profile_id") @NotBlank Long profileId,
                                                             @RequestBody @Valid BusinessProfileUpdateRequest request) {
        return ResponseEntity.ok(businessProfileService.updateProfile(profileId, request));
    }

    /**
     * Deletes the business profile associated with the provided profile ID.
     * <p>
     * This API is responsible for deleting the business profile corresponding to the given profile ID.
     * It may require appropriate privileges to perform the deletion.
     *
     * @param profileId The unique identifier of the profile to be deleted.
     * @return A message indicating the result of the deletion.
     */

    @DeleteMapping("/{profile_id}")
    public ResponseEntity<String> deleteProfile(@PathVariable(name = "profile_id") @NotBlank Long profileId) {
        businessProfileService.deleteProfile(profileId);
        return ResponseEntity.ok("Successfully deleted profile.");
    }

}
