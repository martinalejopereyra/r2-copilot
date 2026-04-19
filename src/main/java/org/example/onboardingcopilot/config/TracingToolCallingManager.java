package org.example.onboardingcopilot.config;

import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class TracingToolCallingManager implements ToolCallingManager {

    private final ToolCallingManager delegate;

    public TracingToolCallingManager(ObservationRegistry observationRegistry) {
        this.delegate = DefaultToolCallingManager.builder()
                .observationRegistry(observationRegistry)
                .build();
    }

    @Override
    public List<ToolDefinition> resolveToolDefinitions(ToolCallingChatOptions chatOptions) {
        return delegate.resolveToolDefinitions(chatOptions);
    }

    @Override
    public ToolExecutionResult executeToolCalls(Prompt prompt, ChatResponse chatResponse) {
        Span span = extractCallerSpan(prompt);
        if (span != null && span.getSpanContext().isValid()) {
            try (Scope ignored = span.makeCurrent()) {
                return delegate.executeToolCalls(prompt, chatResponse);
            }
        }
        return delegate.executeToolCalls(prompt, chatResponse);
    }

    private Span extractCallerSpan(Prompt prompt) {
        if (prompt.getOptions() instanceof ToolCallingChatOptions opts) {
            Object span = opts.getToolContext().get("callerSpan");
            if (span instanceof Span s) return s;
        }
        return null;
    }
}
