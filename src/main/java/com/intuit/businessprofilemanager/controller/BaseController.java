package com.intuit.businessprofilemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    /**
     * Base controller method to return a predefined message, preventing white label error page.
     *
     * @return A fixed string message.
     */
    @RequestMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Business profile manager!");
    }
}
