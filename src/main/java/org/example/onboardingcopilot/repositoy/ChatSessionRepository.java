package org.example.onboardingcopilot.repositoy;

import org.example.onboardingcopilot.model.ChatSession;
import org.example.onboardingcopilot.service.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    Optional<ChatSession> findByPartnerIdAndSessionIdAndSessionType(String partnerId, String sessionId, SessionType sessionType);
    Optional<ChatSession> findFirstBySessionId(String sessionId);
}


