package com.intuit.businessprofilemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @RequestMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Business profile manager!");
    }
}
