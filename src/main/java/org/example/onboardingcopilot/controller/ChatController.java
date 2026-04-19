package org.example.onboardingcopilot.controller;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onboardingcopilot.aspects.PartnerId;
import org.example.onboardingcopilot.service.ChatSessionService;
import org.example.onboardingcopilot.service.OrchestratorService;
import org.example.onboardingcopilot.service.PartnerOnboardingService;
import org.example.onboardingcopilot.service.SessionType;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final OrchestratorService orchestrator;
    private final ChatSessionService chatSessionService;
    private final PartnerOnboardingService partnerOnboardingService;

    @PostMapping
    public SseEmitter chat(
            @PartnerId String securePartnerId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @RequestBody String message,
            HttpServletResponse response) {

        partnerOnboardingService.ensureOnboarding(securePartnerId);

        String resolvedSessionId = (sessionId != null && !sessionId.isBlank()) ? sessionId : null;
        var session = chatSessionService.resolveSession(securePartnerId, resolvedSessionId, SessionType.WEB);

        response.setHeader("X-Trace-Id", Span.current().getSpanContext().getTraceId());
        response.setHeader("X-Session-Id", session.getSessionId());

        SseEmitter emitter = new SseEmitter(120_000L);

        var flux = orchestrator.processInput(securePartnerId, session.getSessionId(), message);

        // Re-inject after AOP finally blocks (GuardrailAspect, ContextPropagationAspect) have
        // cleaned up their Micrometer scopes, which clear trace_id/span_id from MDC.
        Span requestSpan = Span.current();
        if (requestSpan.getSpanContext().isValid()) {
            MDC.put("trace_id", requestSpan.getSpanContext().getTraceId());
            MDC.put("span_id", requestSpan.getSpanContext().getSpanId());
        }

        flux.subscribe(
                        token -> {
                            try {
                                // replace \n with a safe placeholder before SSE sends it
                                String encoded = token.replace("\n", "↵");
                                emitter.send(SseEmitter.event().data(encoded));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("Stream error partner={}: {}", securePartnerId, error.getMessage());
                            emitter.completeWithError(error);
                        },
                        emitter::complete
                );

        return emitter;
    }
}