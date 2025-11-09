package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.integration.PortabilityClient;
import com.example.challenge.repository.ChallengeRepository;
import com.example.challenge.state.ChallengeState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;
    private final AuditService audit;
    private final NotificationService notification;
    private final Map<String, ChallengeState> states;
    private final BssValidationService bssService;
    private final PortabilityClient portabilityClient;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public Challenge createChallenge(String msisdn, String account, String lang) {
        if (!bssService.isRegistered(msisdn)) {
            throw new IllegalStateException("MSISDN not registered in BSS");
        }
        if (!bssService.isActive(msisdn)) {
            throw new IllegalStateException("MSISDN is not active");
        }
        if (repo.existsByMsisdnAndStatusIn(msisdn, List.of(ChallengeStatus.PENDING, ChallengeStatus.ACKNOWLEDGED))) {
            throw new IllegalStateException("Active challenge already exists for this MSISDN");
        }

        Challenge c = new Challenge();
        c.setMsisdn(msisdn);
        c.setAccount(account);
        c.setLanguage(lang);
        c.setStatus(ChallengeStatus.PENDING);
        c.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        repo.save(c);

        audit.record(c, ChallengeStatus.CREATED, "PORTABILITY", "Challenge created and waiting for reply");
        notification.sendChallengeCreated(c);

        ChallengeState state = states.get(ChallengeStatus.PENDING.name());
        if (state != null) state.handle(c);

        return c;
    }

    @Transactional
    public Challenge reply(String msisdn, String reply) {
        Challenge challenge = repo.findByMsisdn(msisdn)
                .orElseThrow(() -> new IllegalStateException("No active challenge found for MSISDN " + msisdn));

        boolean accepted = switch (reply.trim().toUpperCase()) {
            case "OUI", "O", "YES", "Y" -> true;
            case "NON", "NO", "N" -> false;
            default -> {
                auditService.log(challenge.getId(), ChallengeStatus.INVALID_REPLY, "system",
                        "Invalid reply received: " + reply);
                yield false;
            }
        };
        if (accepted) {
            challenge.setStatus(ChallengeStatus.ACCEPTED);
            notificationService.sendConfirmation(challenge, true);
            portabilityClient.notifyChallengeAccepted(challenge);
            auditService.log(challenge.getId(), ChallengeStatus.ACCEPTED, "customer",
                    "Challenge accepted via SMS reply");
        } else if (List.of("NON", "NO", "N").contains(reply.trim().toUpperCase())) {
            challenge.setStatus(ChallengeStatus.REJECTED);
            notificationService.sendConfirmation(challenge, false);
            portabilityClient.notifyChallengeRejected(challenge);
            auditService.log(challenge.getId(), ChallengeStatus.REJECTED, "customer",
                    "Challenge rejected via SMS reply");
        }


        return repo.save(challenge);
    }


    @Transactional
    public void cancel(String msisdn) {
        Challenge c = repo.findByMsisdnAndStatusIn(msisdn,
                List.of(ChallengeStatus.ACKNOWLEDGED, ChallengeStatus.PENDING))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No active challenge found for " + msisdn));

        c.setStatus(ChallengeStatus.CANCELLED);
        notificationService.sendCancellation(c);
        portabilityClient.notifyChallengeCancelled(c);
        repo.save(c);
        audit.record(c, ChallengeStatus.CANCELLED, "PORTABILITY", "Challenge cancelled by API");
    }

    @Transactional
    public void expireChallenges() {
        List<Challenge> expired = repo.findByStatusAndExpiresAtBefore(
            ChallengeStatus.PENDING, LocalDateTime.now());
        for (Challenge c : expired) {
            c.setStatus(ChallengeStatus.EXPIRED);
            repo.save(c);
            audit.record(c, ChallengeStatus.EXPIRED, "SYSTEM", "Challenge expired automatically");
        }
    }
}