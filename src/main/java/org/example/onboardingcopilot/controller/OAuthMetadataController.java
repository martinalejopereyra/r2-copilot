package org.example.onboardingcopilot.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class OAuthMetadataController {

    @Value("${mock.auth.url:http://localhost:9999}")
    private String authUrl;

    @GetMapping("/.well-known/oauth-authorization-server")
    public Map<String, Object> metadata(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return Map.of(
                "issuer", authUrl + "/default",
                "authorization_endpoint", authUrl + "/default/authorize",
                "token_endpoint", authUrl + "/default/token",
                "registration_endpoint", baseUrl + "/register",
                "response_types_supported", List.of("code"),
                "grant_types_supported", List.of("authorization_code"),
                "code_challenge_methods_supported", List.of("S256"),
                "scopes_supported", List.of("openid", "mcp")
        );
    }

    // RFC 9728 — tells the MCP client which auth server protects this resource
    @GetMapping("/.well-known/oauth-protected-resource")
    public Map<String, Object> protectedResource(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return Map.of(
                "resource", baseUrl,
                "authorization_servers", List.of(authUrl + "/default")
        );
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody Map<String, Object> body) {
        return Map.of(
                "client_id", UUID.randomUUID().toString(),
                "client_secret", UUID.randomUUID().toString(),
                "client_id_issued_at", System.currentTimeMillis() / 1000,
                "grant_types", List.of("authorization_code"),
                "response_types", List.of("code"),
                "redirect_uris", body.getOrDefault("redirect_uris", List.of())
        );
    }
}