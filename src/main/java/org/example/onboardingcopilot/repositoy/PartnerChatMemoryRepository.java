
package org.example.onboardingcopilot.repositoy;

import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import org.example.onboardingcopilot.model.ChatSession;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartnerChatMemoryRepository implements ChatMemoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Override
    @Transactional
    public void saveAll(String conversationId, List<Message> messages) {
        if (messages.isEmpty()) return;

        // conversationId IS sessionId now — direct lookup, no partnerId involved
        UUID sessionPk = sessionRepository.findBySessionId(conversationId)
                .map(ChatSession::getId)
                .orElseThrow(() -> new IllegalStateException("No chat session for conversationId: " + conversationId));

        Message latest = messages.getLast();

        String sql = """
                INSERT INTO chat_message (id, session_id, role, content, created_at)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """;

        jdbcTemplate.update(sql,
                UUID.randomUUID(),
                sessionPk,
                latest.getMessageType().getValue(),
                latest.getText(),
                Timestamp.valueOf(LocalDateTime.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findByConversationId(String conversationId) {
        // scoped to this chat session only — not the partner's entire history
        return messageRepository.findAllBySessionSessionIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(h -> switch (h.getRole().toLowerCase()) {
                    case "user" -> (Message) new UserMessage(h.getContent());
                    case "assistant" -> (Message) new AssistantMessage(h.getContent());
                    default -> (Message) new SystemMessage(h.getContent());
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteByConversationId(String conversationId) {
        messageRepository.deleteBySessionSessionId(conversationId);
    }

    @Override
    public List<String> findConversationIds() {
        return List.of();
    }
}

