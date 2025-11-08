package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeHistory;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.repository.ChallengeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final ChallengeHistoryRepository challengeHistoryRepository;

    public AuditService(ChallengeHistoryRepository repo) {
        this.challengeHistoryRepository = repo;
    }

    @Transactional
    public void record(Challenge challenge, ChallengeStatus challengeStatus, String actor, String description) {
        if (challenge == null) {
            return;
        }
        ChallengeHistory h = new ChallengeHistory();
        h.setChallengeId(challenge.getId());
        h.setAction(challengeStatus.toString());
        h.setActor(actor);
        h.setDescription(description);
        h.setTimestamp(LocalDateTime.now());
        challengeHistoryRepository.save(h);
    }
    public List<ChallengeHistory> getHistory(Long challengeId) {
        return challengeHistoryRepository.findByChallengeIdOrderByTimestampDesc(challengeId);
    }
    public void log(Long challengeId, ChallengeStatus challengeStatus, String actor, String description) {
        ChallengeHistory history = new ChallengeHistory();
        history.setChallengeId(challengeId);
        history.setAction(challengeStatus.toString());
        history.setActor(actor);
        history.setDescription(description);
        history.setTimestamp(LocalDateTime.now());
        challengeHistoryRepository.save(history);
    }
}
