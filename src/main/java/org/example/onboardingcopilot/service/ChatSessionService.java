package org.example.onboardingcopilot.service;

import lombok.RequiredArgsConstructor;
import org.example.onboardingcopilot.model.ChatSession;
import org.example.onboardingcopilot.repositoy.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository sessionRepository;

    @Transactional
    public ChatSession resolveSession(String partnerId, String sessionId) {
        if (sessionId != null) {
            return sessionRepository.findBySessionId(sessionId)
                    .filter(s -> s.getPartnerId().equals(partnerId))
                    .orElseGet(() -> createNewSession(partnerId));
        }
        return createNewSession(partnerId);
    }

    @Transactional
    public ChatSession createNewSession(String partnerId) {
        return sessionRepository.save(ChatSession.builder()
                .id(UUID.randomUUID())
                .sessionId(UUID.randomUUID().toString())
                .partnerId(partnerId)
                .build());
    }
}