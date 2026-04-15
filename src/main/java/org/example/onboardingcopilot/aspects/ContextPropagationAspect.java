package org.example.onboardingcopilot.aspects;

import io.opentelemetry.api.baggage.Baggage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ContextPropagationAspect {

    @Around("@annotation(propagateContext)")
    public Object propagate(ProceedingJoinPoint joinPoint, PropagateContext propagateContext)
            throws Throwable {

        Object[] args = joinPoint.getArgs();
        int index = propagateContext.sessionIdArgIndex();

        if (index < args.length && args[index] instanceof String sessionId && sessionId != null) {
            MDC.put("session_id", sessionId);
            Baggage.current().toBuilder()
                    .put("session.id", sessionId)
                    .build()
                    .makeCurrent();
        }

        try {
            return joinPoint.proceed();
        } finally {
            MDC.remove("session_id");
        }
    }
}