package org.example.onboardingcopilot.service;

import lombok.RequiredArgsConstructor;
import org.example.onboardingcopilot.model.OnboardingStatus;
import org.example.onboardingcopilot.model.PartnerOnboarding;
import org.example.onboardingcopilot.repositoy.PartnerOnboardingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerOnboardingService {

    private final PartnerOnboardingRepository partnerOnboardingRepository;

    @Transactional
    public PartnerOnboarding ensureOnboarding(String partnerId) {

        return partnerOnboardingRepository.findByPartnerId(partnerId)
                .orElseGet(() ->
                        partnerOnboardingRepository.save(
                                PartnerOnboarding.builder()
                                        .id(UUID.randomUUID())
                                        .partnerId(partnerId)
                                        .currentStatus(OnboardingStatus.START)
                                        .build()
                        ));
        
    }
}
