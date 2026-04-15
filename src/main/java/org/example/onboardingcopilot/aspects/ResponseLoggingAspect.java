package org.example.onboardingcopilot.aspects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.MDC;

@Aspect
@Component
@Slf4j
public class ResponseLoggingAspect {

    @AfterReturning(
            pointcut = "within(@org.springframework.web.bind.annotation.RestController *) " +
                    "&& within(org.example.onboardingcopilot.controller.MockedOnboardingAPIController)",
            returning = "result"
    )
    public void logResponse(JoinPoint joinPoint, Object result) {
        String partnerId = MDC.get("partner_id");
        if (partnerId == null) return;

        if (result instanceof ResponseEntity<?> responseEntity) {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();

            log.info("Partner : {} {} → {}",
                    partnerId,
                    request.getRequestURI(),
                    responseEntity.getStatusCode().value());
        }
    }
}