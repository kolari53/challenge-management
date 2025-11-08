package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeHistory;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.repository.ChallengeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    private ChallengeHistoryRepository repo;
    private AuditService service;

    @BeforeEach
    void setup() {
        repo = mock(ChallengeHistoryRepository.class);
        service = new AuditService(repo);
    }

    @Test
    void shouldRecordAuditLog() {
        Challenge challenge = new Challenge();
        challenge.setId(1L);

        service.record(challenge, ChallengeStatus.CREATED, "SYSTEM", "Challenge created successfully");

        verify(repo, times(1)).save(any(ChallengeHistory.class));
    }

    @Test
    void shouldNotFailWhenChallengeIsNull() {
        service.record(null, ChallengeStatus.CREATED, "SYSTEM", "No challenge");
        verify(repo, never()).save(any());
    }

    @Test
    void shouldSetAllAuditFieldsProperly() {
        Challenge challenge = new Challenge();
        challenge.setId(42L);

        AuditService realService = new AuditService(repo);
        realService.record(challenge, ChallengeStatus.UPDATED, "ADMIN", "Updated manually");

        verify(repo).save(argThat(history ->
            history.getChallengeId() == 42L &&
                history.getAction().equals(ChallengeStatus.UPDATED) &&
                history.getActor().equals("ADMIN") &&
                history.getDescription().equals("Updated manually") &&
                history.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1))
        ));
    }
}
