package com.example.challenge.strategy;

import com.example.challenge.domain.Challenge;

public interface NotificationStrategy {
    void sendChallengeCreated(Challenge c);
    void sendConfirmation(Challenge c, boolean accepted);
    void sendCancellation(Challenge c);
    void sendExpiration(Challenge c);
}
