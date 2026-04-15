package org.example.onboardingcopilot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuardrailService {

    public boolean isUnsafe(String text) {
        return false;
    }

    public String sanitize(String text) {
        if (text == null) return null;
        return text
                // credit/debit cards
                .replaceAll("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b", "[CARD_REDACTED]")
                // emails
                .replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", "[EMAIL_REDACTED]")
                // IPv4
                .replaceAll("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "[IP_REDACTED]")
                // IPv6
                .replaceAll("([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}", "[IP_REDACTED]")
                // bearer tokens / JWT (three base64 segments separated by dots)
                .replaceAll("Bearer\\s+[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+\\.[A-Za-z0-9\\-_]+", "Bearer [TOKEN_REDACTED]")
                // API keys — common patterns (sk-, pk-, gsk_, etc.)
                .replaceAll("\\b(sk|pk|gsk|api|key)[-_][A-Za-z0-9]{16,}\\b", "[APIKEY_REDACTED]")
                // AWS access keys
                .replaceAll("\\bAKIA[0-9A-Z]{16}\\b", "[AWSKEY_REDACTED]")
                // phone numbers (international format)
                .replaceAll("\\+?\\d[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}\\b", "[PHONE_REDACTED]")
                // CPF (Brazilian national ID — relevant for MercadoLibre/Rappi/PedidosYa)
                .replaceAll("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b", "[CPF_REDACTED]")
                // CNPJ (Brazilian company ID)
                .replaceAll("\\b\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}\\b", "[CNPJ_REDACTED]");
    }
}
