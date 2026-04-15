package org.example.onboardingcopilot.config;

import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestObservationConfig {

    @Bean
    @Primary
    public OpenTelemetry openTelemetry() {
        return OpenTelemetry.noop();
    }
}