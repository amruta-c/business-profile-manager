package com.intuit.businessprofilemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class BusinessProfileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessProfileManagerApplication.class, args);
    }
}
