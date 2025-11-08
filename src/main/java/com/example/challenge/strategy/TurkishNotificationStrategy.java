package com.example.challenge.strategy;

import com.example.challenge.domain.Challenge;
import org.springframework.stereotype.Component;

@Component("tr")
public class TurkishNotificationStrategy implements NotificationStrategy {

    @Override
    public void sendChallengeCreated(Challenge c) {
        System.out.printf("📱 [TR] %s numarasına doğrulama SMS'i gönderildi.%n", c.getMsisdn());
    }

    @Override
    public void sendConfirmation(Challenge c, boolean accepted) {
        System.out.printf("✅ [TR] %s yanıtladı: %s%n", c.getMsisdn(), accepted ? "EVET" : "HAYIR");
    }

    @Override
    public void sendCancellation(Challenge c) {
        System.out.printf("❌ [TR] %s için doğrulama iptal edildi.%n", c.getMsisdn());
    }

    @Override
    public void sendExpiration(Challenge c) {
        System.out.printf("⏰ [TR] %s için doğrulama süresi doldu.%n", c.getMsisdn());
    }
}
