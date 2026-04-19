package org.example.onboardingcopilot.filter;

import io.opentelemetry.api.baggage.Baggage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PartnerContextFilter extends OncePerRequestFilter {


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return request.getRequestURI().startsWith("/dev/");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String partnerId = null;
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            partnerId = jwt.getSubject();
        } else if (auth != null && auth.getPrincipal() instanceof String id) {
            partnerId = id;
        }

        if (partnerId != null) {
            MDC.put("partner_id", partnerId);
        }

        try (var ignored = partnerId != null
                ? Baggage.current().toBuilder().put("partner.id", partnerId).build().makeCurrent()
                : null) {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("partner_id");
        }
    }
}