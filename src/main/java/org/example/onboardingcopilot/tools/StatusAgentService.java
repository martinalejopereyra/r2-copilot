package org.example.onboardingcopilot.tools;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.trace.Span;
import org.example.onboardingcopilot.model.OnboardingStatus;
import org.example.onboardingcopilot.repositoy.PartnerOnboardingRepository;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatusAgentService implements AgentTool {

    private final PartnerOnboardingRepository onboardingRepository;
    private final MeterRegistry meterRegistry;

    @Transactional
    @Tool(description = "Advance partner to next onboarding stage. Only call when logs confirm successful integration.")
    public String markStageAsCompleted(ToolContext toolContext) {
        String partnerId = (String) toolContext.getContext().get("partnerId");
        if (partnerId == null) return "Error: security context missing.";

        return onboardingRepository.findByPartnerId(partnerId)
                .map(onboarding -> {
                    OnboardingStatus current = onboarding.getCurrentStatus();
                    OnboardingStatus next = current.getNext();

                    if (current == next) return "Partner is already LIVE. No action taken.";
                    onboarding.setCurrentStatus(next);

                    meterRegistry.counter("stage.advancement",
                            "from", current.name(),
                            "to", next.name()
                    ).increment();

                    Span.current()
                            .setAttribute("stage.from", current.name())
                            .setAttribute("stage.to", next.name());

                    return "Stage advanced: " + current + " → " + next +
                            ". Partner is now at: " + next.name();
                })
                .orElse("Error: No onboarding record found for partnerId=" + partnerId);
    }
}
