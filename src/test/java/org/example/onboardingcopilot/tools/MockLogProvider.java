package org.example.onboardingcopilot.tools;

import org.example.onboardingcopilot.model.OnboardingStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
public class MockLogProvider implements LogProvider {
    @Override
    public List<String> getLogs(String partnerId, OnboardingStatus status) {
        return switch (partnerId.toLowerCase()) {
            case "ubereats" -> List.of(
                    "[2024-05-10 10:00:01] POST /v1/auth → 401 Invalid Signature — check HMAC key",
                    "[2024-05-10 10:05:22] POST /v1/auth → 401 Invalid Signature — check HMAC key"
            );
            case "rappi" -> List.of(
                    "[2024-05-10 09:00:01] POST /v1/auth → 401 Invalid Signature",
                    "[2024-05-10 09:05:00] POST /v1/auth → 401 Invalid Signature",
                    "[2024-05-10 09:10:00] POST /v1/auth → 401 Token expired"
            );
            case "doordash" -> List.of(
                    "[2024-05-10 11:00:01] POST /v1/webhooks → 200 OK",
                    "[2024-05-10 11:05:00] POST /v1/webhooks → 200 OK",
                    "[2024-05-10 11:10:00] POST /v1/webhooks → 200 OK"
            );
            case "pedidosya" -> List.of(
                    "[2024-05-10 12:00:01] POST /v1/auth → 200 OK",
                    "[2024-05-10 12:00:02] POST /v1/orders → 200 OK",
                    "[2024-05-10 12:00:03] POST /v1/webhooks → 200 OK"
            );
            default -> List.of(
                    "[2024-05-10 10:00:01] POST /v1/auth → 401 Invalid Signature",
                    "[2024-05-10 10:05:22] POST /v1/auth → 401 Invalid Signature",
                    "[2024-05-10 10:10:45] POST /v1/webhooks → 500 Connection Timeout"
            );
        };
    }
}
