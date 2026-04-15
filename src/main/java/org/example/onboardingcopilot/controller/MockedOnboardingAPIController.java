package org.example.onboardingcopilot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("v1")
public class MockedOnboardingAPIController {

    @PostMapping("/webhooks")
    ResponseEntity<String> webhooks(@AuthenticationPrincipal Jwt jwt) {
        String securePartnerId = jwt.getSubject();
        log.info("Partner : {} successfully configured webhooks", securePartnerId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    ResponseEntity<String> auth(@AuthenticationPrincipal Jwt jwt) {
        String securePartnerId = jwt.getSubject();
        log.info("Partner : {} successfully authorized", securePartnerId);
        return ResponseEntity.ok().build();
    }

}
