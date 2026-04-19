package org.example.onboardingcopilot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuardrailService {

    /**
     * Semantic safety check — input only, never called on streaming output tokens.
     */
    public boolean isUnsafe(String text) {
        if (text == null || text.isBlank()) return false;
        // stub — wire LlamaGuard via Ollama in production
        return false;
    }

    /**
     * PII sanitization — called on every output token during streaming.
     * Fast regex only — no LLM calls.
     */
    public String sanitize(String text) {
        if (text == null) return null;
        return text
                .replaceAll("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b", "[CARD_REDACTED]")
                .replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", "[EMAIL_REDACTED]")
                .replaceAll("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "[IP_REDACTED]")
                .replaceAll("([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}", "[IP_REDACTED]")
                .replaceAll("Bearer\\s+[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+", "Bearer [TOKEN_REDACTED]")
                .replaceAll("\\b(sk|pk|gsk|api|key)[-_][A-Za-z0-9]{16,}\\b", "[APIKEY_REDACTED]")
                .replaceAll("\\bAKIA[0-9A-Z]{16}\\b", "[AWSKEY_REDACTED]")
                .replaceAll("\\+?\\d[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}\\b", "[PHONE_REDACTED]")
                .replaceAll("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b", "[CPF_REDACTED]")
                .replaceAll("\\b\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}\\b", "[CNPJ_REDACTED]");
    }
}