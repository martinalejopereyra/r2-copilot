package org.example.onboardingcopilot.model;

public enum OnboardingStatus {

    START(
            """
            Partner is beginning integration. Help them authenticate successfully.
            Auth works via JWT — guide them through credential setup and first auth call.
            When logs show 200 on /v1/auth, advance the stage.
            """
    ),

    AUTH_CONFIGURED(
            """
            Partner has credentials but may be hitting auth errors.
            Common issues: invalid signature, expired tokens, wrong endpoint.
            When logs show consistent 200 on /v1/auth, advance the stage.
            """
    ),

    WEBHOOK_SET(
            """
            Partner is configuring webhooks. Endpoint must be publicly accessible and return 200.
            When logs show consistent 200 on /v1/webhooks, advance the stage.
            """
    ),

    LIVE(
            """
            Partner is fully integrated. Help with production issues only.
            For billing or account questions direct to #r2-support on Slack.
            """
    );

    private final String instructions;

    OnboardingStatus(String instructions) {
        this.instructions = instructions;
    }

    public OnboardingStatus getNext() {
        return switch (this) {
            case START -> AUTH_CONFIGURED;
            case AUTH_CONFIGURED -> WEBHOOK_SET;
            case WEBHOOK_SET -> LIVE;
            case LIVE -> LIVE;
        };
    }

    public String getInstructions() {
        return instructions;
    }
}