package org.example.onboardingcopilot.aspects;

import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RestoreMdcAspect {

    @Around("execution(* org.example.onboardingcopilot.tools.AgentTool+.*(..))")
    public Object restoreMdc(ProceedingJoinPoint joinPoint) throws Throwable {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ToolContext toolContext) {
                String partnerId = (String) toolContext.getContext().get("partnerId");
                String sessionId = (String) toolContext.getContext().get("sessionId");
                if (partnerId != null) MDC.put("partner_id", partnerId);
                if (sessionId != null) MDC.put("session_id", sessionId);
                break;
            }
        }
        // TracingToolCallingManager makes the parent span current before tool execution,
        // so Span.current() here is the tool call child span (same trace_id as parent).
        Span span = Span.current();
        if (span.getSpanContext().isValid()) {
            MDC.put("trace_id", span.getSpanContext().getTraceId());
            MDC.put("span_id", span.getSpanContext().getSpanId());
        }
        return joinPoint.proceed();
    }
}
