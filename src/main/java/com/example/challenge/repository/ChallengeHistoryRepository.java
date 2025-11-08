package com.example.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.challenge.domain.ChallengeHistory;

import java.util.List;

public interface ChallengeHistoryRepository extends JpaRepository<ChallengeHistory, Long> {
    List<ChallengeHistory> findByChallengeIdOrderByTimestampDesc(Long challengeId);
}