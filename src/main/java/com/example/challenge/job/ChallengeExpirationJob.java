package com.example.challenge.job;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.integration.PortabilityClient;
import com.example.challenge.repository.ChallengeRepository;
import com.example.challenge.service.AuditService;
import com.example.challenge.service.ChallengeService;
import com.example.challenge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeExpirationJob {

    private final ChallengeService service;
    private final ChallengeRepository repository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final PortabilityClient portabilityClient;

    @Scheduled(fixedRate = 60000)
    public void expireChallenges() {
        List<Challenge> expired = repository.findByStatusAndExpiresAtBefore(ChallengeStatus.PENDING, LocalDateTime.now());
        expired.forEach(c -> {
            c.setStatus(ChallengeStatus.EXPIRED);
            repository.save(c);
            auditService.log(Long.valueOf(c.getMsisdn()), ChallengeStatus.EXPIRED, "Expired_Job","Challenge expired automatically");
            notificationService.sendExpiration(c);
            portabilityClient.notifyChallengeExpired(c);
        });
    }
}
