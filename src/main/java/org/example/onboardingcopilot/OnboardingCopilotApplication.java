package org.example.onboardingcopilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class OnboardingCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnboardingCopilotApplication.class, args);
    }

}
