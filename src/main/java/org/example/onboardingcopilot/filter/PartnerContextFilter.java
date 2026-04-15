package org.example.onboardingcopilot.filter;

import io.opentelemetry.api.baggage.Baggage;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;

public class PartnerContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String partnerId = jwt.getSubject();


            Baggage.current().toBuilder()
                    .put("partner.id", partnerId)
                    .build()
                    .makeCurrent();

            MDC.put("partner_id", partnerId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("partner_id");
        }
    }
}