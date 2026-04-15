package org.example.onboardingcopilot.repositoy;

import org.example.onboardingcopilot.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllBySessionSessionIdOrderByCreatedAtAsc(String sessionId);
    void deleteBySessionSessionId(String sessionId);
}