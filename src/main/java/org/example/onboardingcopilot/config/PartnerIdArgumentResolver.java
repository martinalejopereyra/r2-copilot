package org.example.onboardingcopilot.config;

import org.example.onboardingcopilot.aspects.PartnerId;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

@Component
public class PartnerIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PartnerId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        // works for both JWT and API key — both filters set this
        String partnerId = MDC.get("partner_id");
        if (partnerId != null) return partnerId;

        // fallback to security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("No authentication found");

        if (auth.getPrincipal() instanceof Jwt jwt) return jwt.getSubject();
        if (auth.getPrincipal() instanceof String s) return s;

        throw new IllegalStateException("Cannot resolve partnerId from principal: "
                + auth.getPrincipal().getClass());
    }
}
