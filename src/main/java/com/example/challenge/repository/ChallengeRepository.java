package com.example.challenge.repository;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Optional<Challenge> findByMsisdnAndStatusIn(String msisdn, List<ChallengeStatus> statuses);

    List<Challenge> findByStatusAndExpiresAtBefore(ChallengeStatus status, LocalDateTime time);

    List<Challenge> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);
    Optional<Challenge> findByMsisdn(String msisdn);
    boolean existsByMsisdnAndStatusIn(String msisdn, List<ChallengeStatus> statuses);



}
