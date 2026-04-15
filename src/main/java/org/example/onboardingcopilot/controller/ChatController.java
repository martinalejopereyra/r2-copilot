package org.example.onboardingcopilot.controller;

import io.opentelemetry.api.trace.Span;
import org.example.onboardingcopilot.service.ChatSessionService;
import org.example.onboardingcopilot.service.OrchestratorService;
import org.example.onboardingcopilot.service.PartnerOnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final OrchestratorService orchestrator;
    private final ChatSessionService chatSessionService;
    private final PartnerOnboardingService partnerOnboardingService;

    @PostMapping
    public ResponseEntity<String> chat(@AuthenticationPrincipal Jwt jwt, @RequestHeader(value = "X-Session-Id", required = false) String sessionId, @RequestBody String message) {

        String securePartnerId = jwt.getSubject();
        partnerOnboardingService.ensureOnboarding(securePartnerId);

        String resolvedSessionId = (sessionId != null && !sessionId.isBlank()) ? sessionId : null;
        var session = chatSessionService.resolveSession(securePartnerId, resolvedSessionId);

        String result = orchestrator.processInput(securePartnerId, session.getSessionId(), message);

        return ResponseEntity.ok()
                .header("X-Trace-Id", Span.current().getSpanContext().getTraceId())
                .header("X-Session-Id", session.getSessionId())
                .body(result);

    }
}

