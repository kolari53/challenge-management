package com.example.challenge.strategy;

import com.example.challenge.domain.Challenge;
import org.springframework.stereotype.Component;

@Component("en")
public class EnglishNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendChallengeCreated(Challenge c) {
        System.out.printf("📱 [EN] Verification SMS sent to %s%n", c.getMsisdn());
    }

    @Override
    public void sendConfirmation(Challenge c, boolean accepted) {
        System.out.printf("✅ [EN] %s replied: %s%n", c.getMsisdn(), accepted ? "YES" : "NO");
    }

    @Override
    public void sendCancellation(Challenge c) {
        System.out.printf("❌ [EN] Challenge cancelled for %s%n", c.getMsisdn());
    }

    @Override
    public void sendExpiration(Challenge c) {
        System.out.printf("⏰ [EN] Challenge expired for %s%n", c.getMsisdn());
    }
}
