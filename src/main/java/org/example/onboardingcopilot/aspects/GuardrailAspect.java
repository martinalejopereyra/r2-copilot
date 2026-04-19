package org.example.onboardingcopilot.aspects;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.onboardingcopilot.service.GuardrailService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GuardrailAspect {

    private final GuardrailService guardrailService;
    private final MeterRegistry meterRegistry;
    private final Tracer tracer;

    private static final String BLOCKED_INPUT_RESPONSE =
            "That request falls outside what I can help with. " +
                    "I am here to assist with your R2 technical integration — " +
                    "feel free to ask about API setup, authentication, webhooks, or any integration errors.";

    @Around("@annotation(Guardrailed)")
    public Object applyGuardrails(ProceedingJoinPoint joinPoint) throws Throwable {
        Span span = tracer.nextSpan().name("guardrail.check").start();

        try (var ignored = tracer.withSpan(span)) {
            span.tag("guardrail.method", joinPoint.getSignature().toShortString());

            // INPUT — full semantic check + PII sanitize before LLM runs
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String text) {
                    if (guardrailService.isUnsafe(text)) {
                        log.warn("Guardrail blocked input at {}", joinPoint.getSignature());
                        span.tag("guardrail.blocked", "input");
                        meterRegistry.counter("guardrail.blocked", "direction", "input").increment();
                        return Flux.just(BLOCKED_INPUT_RESPONSE);
                    }
                    args[i] = guardrailService.sanitize(text);
                }
            }

            Object result = joinPoint.proceed(args);

            // OUTPUT — PII sanitize per token only, no semantic check
            if (result instanceof Flux<?> flux) {
                return flux.map(chunk -> {
                    if (chunk instanceof String text) {
                        return guardrailService.sanitize(text);
                    }
                    return chunk;
                });
            }

            // fallback for non-streaming paths
            if (result instanceof String text) {
                return guardrailService.sanitize(text);
            }

            return result;

        } finally {
            span.end();
        }
    }
}