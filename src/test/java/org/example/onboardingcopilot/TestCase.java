package org.example.onboardingcopilot;

public record TestCase(
        String partnerId,
        String stage,
        String question,
        String expectedBehavior,
        String shouldCallTool
) {
}