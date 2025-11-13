package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.integration.PortabilityClient;
import com.example.challenge.repository.ChallengeRepository;
import com.example.challenge.state.ChallengeState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChallengeServiceTest {

    private ChallengeRepository repo;
    private AuditService auditService;
    private NotificationService notificationService;
    private BssValidationService bssService;
    private PortabilityClient portabilityClient;
    private ChallengeService service;

    @BeforeEach
    void setup() {
        repo = mock(ChallengeRepository.class);
        auditService = mock(AuditService.class);
        notificationService = mock(NotificationService.class);
        bssService = mock(BssValidationService.class);
        portabilityClient = mock(PortabilityClient.class);

        ChallengeState pendingState = mock(ChallengeState.class);
        Map<String, ChallengeState> states = Map.of("PENDING", pendingState);

        service = new ChallengeService(
                repo,
                auditService,
                notificationService,
                states,
                bssService,
                portabilityClient,
                notificationService,
                auditService
        );
    }

    @Test
    void shouldCreateChallengeSuccessfully() throws InterruptedException {
        when(bssService.isRegistered(anyString())).thenReturn(true);
        when(bssService.isActive(anyString())).thenReturn(true);
        when(repo.existsByMsisdnAndStatusIn(anyString(), anyList())).thenReturn(false);

        Challenge created = service.createChallenge("5550001", "ACC01", "tr");

        assertThat(created.getMsisdn()).isEqualTo("5550001");
        assertThat(created.getStatus()).isEqualTo(ChallengeStatus.PENDING);
        verify(repo).save(any(Challenge.class));
        verify(notificationService).sendChallengeCreated(any());
    }

    @Test
    void shouldThrowWhenBssValidationFails() {
        when(bssService.isRegistered(anyString())).thenReturn(false);
        assertThrows(IllegalStateException.class,
                () -> service.createChallenge("5550001", "ACC01", "tr"));
    }

    @Test
    void shouldProcessYesReply() {
        Challenge c = new Challenge();
        c.setMsisdn("5551111");
        c.setStatus(ChallengeStatus.PENDING);
        when(repo.findByMsisdn(anyString())).thenReturn(Optional.of(c));

        service.reply("5551111", "YES");

        assertThat(c.getStatus()).isEqualTo(ChallengeStatus.ACCEPTED);
        verify(portabilityClient).notifyChallengeAccepted(c);
        verify(notificationService).sendConfirmation(c, true);
        verify(auditService).log(eq(c.getId()), eq(ChallengeStatus.ACCEPTED), eq("customer"), anyString());
    }

    @Test
    void shouldProcessNoReply() {
        Challenge c = new Challenge();
        c.setMsisdn("5552222");
        c.setStatus(ChallengeStatus.PENDING);
        when(repo.findByMsisdn(anyString())).thenReturn(Optional.of(c));

        service.reply("5552222", "NO");

        assertThat(c.getStatus()).isEqualTo(ChallengeStatus.REJECTED);
        verify(portabilityClient).notifyChallengeRejected(c);
        verify(notificationService).sendConfirmation(c, false);
        verify(auditService).log(eq(c.getId()), eq(ChallengeStatus.REJECTED), eq("customer"), anyString());
    }

    @Test
    void shouldCancelActiveChallenge() {
        Challenge c = new Challenge();
        c.setMsisdn("5554444");
        c.setStatus(ChallengeStatus.PENDING);
        when(repo.findByMsisdnAndStatusIn(anyString(), anyList()))
                .thenReturn(Optional.of(c));

        service.cancel("5554444");

        assertThat(c.getStatus()).isEqualTo(ChallengeStatus.CANCELLED);
        verify(repo).save(c);
        verify(auditService).record(c, ChallengeStatus.CANCELLED, "PORTABILITY", "Challenge cancelled by API");
    }

    @Test
    void shouldThrowWhenCancelWithoutActiveChallenge() {
        when(repo.findByMsisdnAndStatusIn(anyString(), anyList()))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.cancel("5559999"));
    }

    @Test
    void shouldExpirePendingChallenges() {
        Challenge expiring = new Challenge();
        expiring.setMsisdn("5556666");
        expiring.setStatus(ChallengeStatus.PENDING);
        expiring.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(repo.findByStatusAndExpiresAtBefore(eq(ChallengeStatus.PENDING), any()))
                .thenReturn(List.of(expiring));

        service.expireChallenges();

        assertThat(expiring.getStatus()).isEqualTo(ChallengeStatus.EXPIRED);
        verify(auditService).record(expiring, ChallengeStatus.EXPIRED, "SYSTEM", "Challenge expired automatically");
        verify(repo).save(expiring);
    }
}
