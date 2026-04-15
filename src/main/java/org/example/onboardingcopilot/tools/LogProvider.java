package org.example.onboardingcopilot.tools;

import org.example.onboardingcopilot.model.OnboardingStatus;

import java.util.List;

public interface LogProvider {
    List<String> getLogs(String partnerId, OnboardingStatus status);
}
