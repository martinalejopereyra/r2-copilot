package org.example.onboardingcopilot.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.onboardingcopilot.service.SessionType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ephemeral — one per chat window, holds conversation memory
@Entity
@Table(name = "chat_session", uniqueConstraints = {
        @UniqueConstraint(name = "uq_chat_session_partner_type", columnNames = {"partner_id", "session_id", "session_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String sessionId;       // this is the conversationId for memory

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionType sessionType = SessionType.WEB;

    @Column(nullable = false)
    private String partnerId;       // links back to PartnerOnboarding

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;  // null while active

    @Builder.Default
    @OneToMany(mappedBy = "session")
    private List<ChatMessage> history = new ArrayList<>();
}
