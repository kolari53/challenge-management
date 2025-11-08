package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.strategy.NotificationStrategy;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class NotificationService {

    private final Map<String, NotificationStrategy> strategies;

    public NotificationService(Map<String, NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    private NotificationStrategy select(Challenge c) {
        return strategies.getOrDefault(c.getPreferredLanguage(), strategies.get("en"));
    }

    public void sendChallengeCreated(Challenge c) {
        select(c).sendChallengeCreated(c);
    }

    public void sendConfirmation(Challenge c, boolean accepted) {
        select(c).sendConfirmation(c, accepted);
    }

    public void sendCancellation(Challenge c) {
        select(c).sendCancellation(c);
    }

    public void sendExpiration(Challenge c) {
        select(c).sendExpiration(c);
    }
}
