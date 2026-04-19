package org.example.onboardingcopilot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "partner_api_key")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerApiKey {

    @Id
    private UUID id;

    @Column(name = "partner_id", nullable = false)
    private String partnerId;

    @Column(name = "hashed_key", nullable = false, unique = true)
    private String hashedKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "active", nullable = false)
    private boolean active;
}