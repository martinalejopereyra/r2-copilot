package org.example.onboardingcopilot.config;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import lombok.RequiredArgsConstructor;
import org.example.onboardingcopilot.tools.McpIntegrationTool;
import org.springframework.ai.mcp.server.webmvc.transport.WebMvcSseServerTransportProvider;
import org.springframework.ai.mcp.server.webmvc.transport.WebMvcStreamableServerTransportProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class McpConfig {

    private final McpIntegrationTool mcpIntegrationTool;

    @Value("${mcp.base-url:http://localhost:8080}")
    private String mcpBaseUrl;

    @Bean
    public McpServerFeatures.SyncToolSpecification mcpTools() {
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(mcpIntegrationTool.toolDefinition())
                .callHandler(mcpIntegrationTool::handle)
                .build();
    }

    // Legacy SSE transport — for clients that send GET first
    @Bean
    public WebMvcSseServerTransportProvider sseTransportProvider() {
        return WebMvcSseServerTransportProvider.builder()
                .sseEndpoint("/mcp/sse")
                .messageEndpoint("/mcp/messages")
                .baseUrl(mcpBaseUrl)
                .contextExtractor(this::extractContext)
                .build();
    }

    @Bean("mcpSseServer")
    public McpSyncServer mcpSseServer(WebMvcSseServerTransportProvider sseTransportProvider,
                                      McpServerFeatures.SyncToolSpecification mcpTools) {
        return McpServer.sync(sseTransportProvider).tools(mcpTools).build();
    }

    // Streamable HTTP transport — for clients that POST first (OAuth2 + API key)
    @Bean
    public WebMvcStreamableServerTransportProvider streamableTransportProvider() {
        return WebMvcStreamableServerTransportProvider.builder()
                .mcpEndpoint("/mcp/sse")
                .contextExtractor(this::extractContext)
                .build();
    }

    @Bean("mcpStreamableServer")
    public McpSyncServer mcpStreamableServer(WebMvcStreamableServerTransportProvider streamableTransportProvider,
                                             McpServerFeatures.SyncToolSpecification mcpTools) {
        return McpServer.sync(streamableTransportProvider).tools(mcpTools).build();
    }

    @Bean
    public RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransportProvider sseTransportProvider,
                                                            WebMvcStreamableServerTransportProvider streamableTransportProvider) {
        return sseTransportProvider.getRouterFunction().and(streamableTransportProvider.getRouterFunction());
    }

    private McpTransportContext extractContext(ServerRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return McpTransportContext.EMPTY;
        if (auth.getPrincipal() instanceof Jwt jwt) {
            return McpTransportContext.create(Map.of("partner_id", jwt.getSubject()));
        }
        if (auth.getPrincipal() instanceof String partnerId) {
            return McpTransportContext.create(Map.of("partner_id", partnerId));
        }
        return McpTransportContext.EMPTY;
    }
}
