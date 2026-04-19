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
    public ChatSession resolveSession(String partnerId, String sessionId, SessionType sessionType) {
        if (sessionId != null) {
            return sessionRepository.findByPartnerIdAndSessionIdAndSessionType(partnerId, sessionId, sessionType)
                    .orElseGet(() -> sessionRepository.save(ChatSession.builder()
                            .id(UUID.randomUUID())
                            .sessionId(sessionId)
                            .sessionType(sessionType)
                            .partnerId(partnerId)
                            .build()));
        }
        return createNewSession(partnerId, sessionType);
    }

    @Transactional
    public ChatSession createNewSession(String partnerId, SessionType sessionType) {
        return sessionRepository.save(ChatSession.builder()
                .id(UUID.randomUUID())
                .sessionId(UUID.randomUUID().toString())
                .sessionType(sessionType)
                .partnerId(partnerId)
                .build());
    }
}