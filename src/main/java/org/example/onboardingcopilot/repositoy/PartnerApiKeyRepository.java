package org.example.onboardingcopilot.repositoy;

import org.example.onboardingcopilot.model.PartnerApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartnerApiKeyRepository extends JpaRepository<PartnerApiKey, UUID> {
    Optional<PartnerApiKey> findByHashedKeyAndActiveTrue(String hashedKey);
}