package org.example.onboardingcopilot.tools;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onboardingcopilot.model.OnboardingStatus;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosticAgentService implements AgentTool {

    private final MeterRegistry meterRegistry;
    private final LogProvider logProvider;

    @Tool(description = "Get latest integration logs for the current partner.")
    public String getLatestLogs(ToolContext toolContext) {
        String partnerId = (String) toolContext.getContext().get("partnerId");
        String currentStatus = (String) toolContext.getContext().get("currentStatus");

        if (partnerId == null) return "Error: security context missing.";

        meterRegistry.counter("diagnostic.agent.calls").increment();
        log.info("DiagnosticAgent fetching logs for partner={} stage={}", partnerId, currentStatus);

        OnboardingStatus status = currentStatus != null
                ? OnboardingStatus.valueOf(currentStatus)
                : OnboardingStatus.START;

        List<String> logs = logProvider.getLogs(partnerId, status);

        if (logs.isEmpty()) {
            meterRegistry.counter("diagnostic.agent.empty.results").increment();
            Span.current().setAttribute("logs.empty", true);
            return "No logs found for this partner in the last hour.";
        }

        Span.current().setAttribute("logs.empty", false);
        return String.join("\n", logs);
    }
}

