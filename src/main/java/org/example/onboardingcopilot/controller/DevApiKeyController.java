package org.example.onboardingcopilot.controller;

import lombok.RequiredArgsConstructor;
import org.example.onboardingcopilot.model.PartnerApiKey;
import org.example.onboardingcopilot.repositoy.PartnerApiKeyRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile("!prod")
public class DevApiKeyController {

    private final PartnerApiKeyRepository apiKeyRepository;

    @PostMapping("/api-key/{partnerId}")
    public Map<String, String> createApiKey(@PathVariable String partnerId) throws Exception {
        String rawKey = UUID.randomUUID().toString();
        String hashedKey = hash(rawKey);

        apiKeyRepository.save(PartnerApiKey.builder()
                .id(UUID.randomUUID())
                .partnerId(partnerId)
                .hashedKey(hashedKey)
                .createdAt(Instant.now())
                .active(true)
                .build());

        return Map.of("partnerId", partnerId, "apiKey", rawKey);
    }

    private String hash(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}