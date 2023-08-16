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
    private final Counter validationApiSuccessCount;
    private final Counter validationApiFailureCount;
    private final Counter invalidDataExceptionCount;
    private final Counter dataNotFoundExceptionCount;
    private final Counter repositoryExceptionCount;
    private final Counter totalSubscriptionCount;
    private final Counter totalUnsubscriptionCount;

    public AppMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        validationApiSuccessCount = meterRegistry.counter("business-profile-manager.validation.success.count");
        validationApiFailureCount = meterRegistry.counter("business-profile-manager.validation.failure.count");
        invalidDataExceptionCount = meterRegistry.counter(
                "business-profile-manager.validation.invalid-data-exception.count");
        dataNotFoundExceptionCount = meterRegistry.counter(
                "business-profile-manager.db-search.data-not-found-exception.count");
        repositoryExceptionCount = meterRegistry.counter(
                "business-profile-manager.db-search.repository-exception.count");
        totalSubscriptionCount = meterRegistry.counter("business-profile-manager.endpoint.total-subscription.count");
        totalUnsubscriptionCount = meterRegistry.counter("business-profile-manager.endpoint.total-unsubscription.count");
    }

    @Bean(name = "TimedAspect")
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    public void incrementValidationApiSuccessCount() {
        this.validationApiSuccessCount.increment();
    }

    public void incrementValidationApiFailureCount() {
        this.validationApiFailureCount.increment();
    }

    public void incrementInvalidDataExceptionCount() {
        this.invalidDataExceptionCount.increment();
    }

    public void incrementDataNotFoundCount() {
        this.dataNotFoundExceptionCount.increment();
    }

    public void incrementRepositoryExceptionCount() {
        this.repositoryExceptionCount.increment();
    }

    public void incrementSubscriptionCount() {
        this.totalSubscriptionCount.increment();
    }

    public void incrementUnsubscriptionCount() {
        this.totalUnsubscriptionCount.increment();
    }
}
