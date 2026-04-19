package org.example.onboardingcopilot.filter;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // Capture span before chain.doFilter() — for SSE Spring closes the span scope
        // when the async context starts, so Span.current() is a no-op on the outbound path.
        Span requestSpan = Span.current();
        boolean validSpan = requestSpan.getSpanContext().isValid();

        chain.doFilter(request, response);

        String partnerId = MDC.get("partner_id");
        if (partnerId != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (validSpan) {
                MDC.put("trace_id", requestSpan.getSpanContext().getTraceId());
                MDC.put("span_id", requestSpan.getSpanContext().getSpanId());
            }
            try {
                log.info("Partner : {} {} {} → {}",
                        partnerId,
                        httpRequest.getMethod(),
                        httpRequest.getRequestURI(),
                        httpResponse.getStatus());
            } finally {
                MDC.remove("trace_id");
                MDC.remove("span_id");
            }
        }
    }
}
