package com.example.challenge.strategy;

import com.example.challenge.domain.Challenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("email")
public class EmailNotificationStrategy implements NotificationStrategy {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationStrategy.class);

    @Override
    public void sendChallengeCreated(Challenge c) {
        log.info("[EMAIL] Challenge created for account {} / {}", c.getAccount(), c.getMsisdn());
    }

    @Override
    public void sendConfirmation(Challenge c, boolean accepted) {
        log.info("[EMAIL] Challenge {} confirmation sent to account {}", accepted ? "ACCEPTED" : "REJECTED", c.getAccount());
    }

    @Override
    public void sendCancellation(Challenge c) {
        log.info("[EMAIL] Challenge cancelled for account {}", c.getAccount());
    }

    @Override
    public void sendExpiration(Challenge c) {
        log.info("[EMAIL] Challenge expired for account {}", c.getAccount());
    }
}
