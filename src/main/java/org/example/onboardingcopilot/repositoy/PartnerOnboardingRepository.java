package org.example.onboardingcopilot.repositoy;

import org.example.onboardingcopilot.model.PartnerOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartnerOnboardingRepository extends JpaRepository<PartnerOnboarding, UUID> {
    Optional<PartnerOnboarding> findByPartnerId(String partnerId);
}
