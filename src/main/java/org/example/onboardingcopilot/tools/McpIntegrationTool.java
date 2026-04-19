package org.example.onboardingcopilot.tools;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onboardingcopilot.service.ChatSessionService;
import org.example.onboardingcopilot.service.OrchestratorService;
import org.example.onboardingcopilot.service.PartnerOnboardingService;
import org.example.onboardingcopilot.service.SessionType;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpIntegrationTool {

    private final OrchestratorService orchestratorService;
    private final ChatSessionService chatSessionService;
    private final PartnerOnboardingService partnerOnboardingService;

    public McpSchema.Tool toolDefinition() {
        return McpSchema.Tool.builder()
                .name("askIntegrationEngineer")
                .description("""
                        Ask the R2 Senior Integration Engineer a question about your integration.
                        Use for API auth issues, webhook config, integration errors, R2 API questions.
                        Include relevantCode if you have the implementation — improves diagnosis.
                        """)
                .inputSchema(new McpSchema.JsonSchema(
                        "object",
                        Map.of(
                                "question", new McpSchema.JsonSchema("string", null, null, null, null, null),
                                "relevantCode", new McpSchema.JsonSchema("string", null, null, null, null, null)
                        ),
                        List.of("question"),
                        null, null, null
                ))
                .build();
    }

    public McpSchema.CallToolResult handle(
            McpSyncServerExchange exchange,
            McpSchema.CallToolRequest request) {

        String partnerFromOAuth = (String) exchange.transportContext().get("partner_id");
        String partnerId = partnerFromOAuth != null ? partnerFromOAuth : MDC.get("partner_id");
        SessionType sessionType = partnerFromOAuth != null ? SessionType.MCP_OAUTH : SessionType.MCP_API_KEY;

        if (partnerId == null) {
            return errorResult("Authentication required. Configure your JWT or API key.");
        }

        partnerOnboardingService.ensureOnboarding(partnerId);

        String question = (String) request.arguments().get("question");
        String relevantCode = (String) request.arguments().get("relevantCode");

        if (question == null || question.isBlank()) {
            return errorResult("question is required.");
        }

        log.info("MCP call partner={} hasCode={}", partnerId, relevantCode != null);

        String enrichedQuestion = buildQuestion(question, relevantCode);
        String sessionId = chatSessionService.resolveSession(partnerId, exchange.sessionId(), sessionType).getSessionId();

        String response = orchestratorService.processInput(partnerId, sessionId, enrichedQuestion)
                .collectList()
                .map(chunks -> String.join("", chunks))
                .block();

        return McpSchema.CallToolResult.builder()
                .content(List.of(new McpSchema.TextContent(response)))
                .isError(false)
                .build();
    }

    private String buildQuestion(String question, String relevantCode) {
        if (relevantCode == null || relevantCode.isBlank()) return question;
        return """
                %s

                Relevant code: %s
                """.formatted(question, relevantCode);
    }

    private McpSchema.CallToolResult errorResult(String message) {
        return McpSchema.CallToolResult.builder()
                .content(List.of(new McpSchema.TextContent(message)))
                .isError(true)
                .build();
    }
}
