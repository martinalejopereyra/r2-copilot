package org.example.onboardingcopilot.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onboardingcopilot.repositoy.PartnerApiKeyRepository;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private final PartnerApiKeyRepository apiKeyRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/mcp/") && !uri.equals("/mcp");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-Api-Key");
        if (apiKey == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                apiKey = authHeader.substring(7);
            }
        }

        if (apiKey != null) {
            try {
                String hashedKey = hashKey(apiKey);
                apiKeyRepository.findByHashedKeyAndActiveTrue(hashedKey)
                        .ifPresent(key -> {
                            MDC.put("partner_id", key.getPartnerId());
                            SecurityContextHolder.getContext()
                                    .setAuthentication(
                                            new UsernamePasswordAuthenticationToken(
                                                    key.getPartnerId(),
                                                    null,
                                                    List.of(new SimpleGrantedAuthority("ROLE_PARTNER"))
                                            )
                                    );
                            log.debug("API key auth for partner={}", key.getPartnerId());
                        });
            } catch (Exception e) {
                log.warn("Invalid API key: {}", e.getMessage());
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("partner_id");
        }
    }

    private String hashKey(String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}