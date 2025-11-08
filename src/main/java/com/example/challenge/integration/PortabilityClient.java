package com.example.challenge.integration;

import com.example.challenge.domain.Challenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PortabilityClient {
    private static final Logger log = LoggerFactory.getLogger(PortabilityClient.class);

    public void notifyChallengeAccepted(Challenge challenge) {
        log.info("[PORTABILITY] Challenge accepted for msisdn {}", challenge.getMsisdn());
    }

    public void notifyChallengeRejected(Challenge challenge) {
        log.info("[PORTABILITY] Challenge rejected for msisdn {}", challenge.getMsisdn());
    }

    public void notifyChallengeCancelled(Challenge challenge) {
        log.info("[PORTABILITY] Challenge cancelled for msisdn {}", challenge.getMsisdn());
    }

    public void notifyChallengeExpired(Challenge challenge) {
        log.info("[PORTABILITY] Challenge expired for msisdn {}", challenge.getMsisdn());
    }
}
