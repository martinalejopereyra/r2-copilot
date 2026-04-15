package org.example.onboardingcopilot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "partner_onboarding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerOnboarding {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String partnerId;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus currentStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_json")
    private Map<String, Object> contextData;
}