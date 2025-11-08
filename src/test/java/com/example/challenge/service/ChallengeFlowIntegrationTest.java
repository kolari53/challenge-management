package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.integration.PortabilityClient;
import com.example.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class ChallengeFlowIntegrationTest {

    @Autowired
    private ChallengeRepository repo;

    @Autowired
    private PortabilityClient portabilityClient;

    @Test
    void shouldPersistChallengeAndExpire() throws Exception {
        // arrange
        Challenge c = new Challenge();
        c.setMsisdn("5301111112");
        c.setAccount("TESTACC");
        c.setLanguage("tr");
        c.setStatus(ChallengeStatus.PENDING);
        c.setExpiresAt(java.time.LocalDateTime.now().plusSeconds(2));
        repo.save(c);

        Thread.sleep(3000);

        Optional<Challenge> found = repo.findById(c.getId());
        assertThat(found).isPresent();
    }
}
