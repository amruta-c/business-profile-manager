package com.intuit.businessprofilemanager.controller;

import com.intuit.businessprofilemanager.model.BusinessProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class BusinessProfileController {

    //read
    @GetMapping("/{profileId}")
    public void getBusinessProfile(@PathVariable String profileId) {
        // TODO document why this method is empty
    }

    //put
    @PutMapping("/{profileId}")
    public void updateProfile(@PathVariable String profileId, @RequestBody BusinessProfile profile) {
        // TODO document why this method is empty
    }

    //patch
//    @PatchMapping("/{id}")
//    public void updateProfile() {
//
//    }


}
