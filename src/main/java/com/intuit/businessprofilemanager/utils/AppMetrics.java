package com.intuit.businessprofilemanager.utils;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class AppMetrics {
    private final MeterRegistry meterRegistry;
    private final Counter VALIDATION_API_SUCCESS;
    private final Counter VALIDATION_API_FAILURE;
    private final Counter INVALID_DATA_EXCEPTION;
    private final Counter DATA_NOT_FOUND_EXCEPTION;
    private final Counter SUBSCRIPTION_COUNT;
    private final Counter UNSUBSCRIPTION_COUNT;

    public AppMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        VALIDATION_API_SUCCESS = meterRegistry.counter("business-profile-manager.validation.success.count");
        VALIDATION_API_FAILURE = meterRegistry.counter("business-profile-manager.validation.failure.count");
        INVALID_DATA_EXCEPTION = meterRegistry.counter("business-profile-manager.validation.invalid-data-exception.count");
        DATA_NOT_FOUND_EXCEPTION = meterRegistry.counter("business-profile-manager.db-search.data-not-found-exception.count");
        SUBSCRIPTION_COUNT = meterRegistry.counter("business-profile-manager.endpoint.subscription.count");
        UNSUBSCRIPTION_COUNT = meterRegistry.counter("business-profile-manager.endpoint.unsubscription.count");
    }

    @Bean(name = "TimedAspect")
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    public void incrementVALIDATION_API_SUCCESS() {
        this.VALIDATION_API_SUCCESS.increment();
    }

    public void incrementVALIDATION_API_FAILURE() {
        this.VALIDATION_API_FAILURE.increment();
    }

    public void incrementINVALID_DATA_EXCEPTION() {
        this.INVALID_DATA_EXCEPTION.increment();
    }

    public void incrementDATA_NOT_FOUND() {
        this.DATA_NOT_FOUND_EXCEPTION.increment();
    }

    public void incrementSUBSCRIPTION_COUNT() {
        this.SUBSCRIPTION_COUNT.increment();
    }

    public void incrementUNSUBSCRIPTION_COUNT() {
        this.UNSUBSCRIPTION_COUNT.increment();
    }
}
