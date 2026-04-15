package org.example.onboardingcopilot.aspects;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.onboardingcopilot.service.GuardrailService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GuardrailAspect {

    private final GuardrailService guardrailService;
    private final MeterRegistry meterRegistry;

    @Around("@annotation(Guardrailed)")
    public Object applyGuardrails(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("guardrail.validate Validating input");

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String text && guardrailService.isUnsafe(text)) {
                log.warn("Guardrail blocked input at {}", joinPoint.getSignature());
                meterRegistry.counter("guardrail.blocked", "direction", "input").increment();
                return "That request falls outside what I can help with. " +
                       "I'm here to assist with your R2 technical integration — " +
                       "feel free to ask about API setup, authentication, webhooks, or any integration errors.";
            }
        }

        Object result = joinPoint.proceed();

        log.info("guardrail.validate Validating output");

        if (result instanceof String text) {
            String sanitized = guardrailService.sanitize(text);
            if (guardrailService.isUnsafe(sanitized)) {
                log.warn("Guardrail blocked output at {}", joinPoint.getSignature());
                meterRegistry.counter("guardrail.blocked", "direction", "output").increment();
                return "I encountered an issue generating a response. " +
                       "Please try rephrasing, or reach out to #r2-support on Slack if the issue persists.";
            }
            return sanitized;
        }

        return result;
    }
}
