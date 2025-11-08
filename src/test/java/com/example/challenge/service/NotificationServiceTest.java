package com.example.challenge.service;

import com.example.challenge.domain.Challenge;
import com.example.challenge.strategy.NotificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationStrategy trStrategy;
    private NotificationStrategy enStrategy;
    private NotificationService service;

    @BeforeEach
    void setup() {
        trStrategy = mock(NotificationStrategy.class);
        enStrategy = mock(NotificationStrategy.class);

        service = new NotificationService(Map.of(
            "tr", trStrategy,
            "en", enStrategy
        ));
    }

    @Test
    void shouldUseTurkishStrategyWhenLangTr() {
        Challenge c = new Challenge();
        c.setLanguage("tr");
        c.setMsisdn("5550001");

        service.sendChallengeCreated(c);

        verify(trStrategy, times(1)).sendChallengeCreated(c);
        verify(enStrategy, never()).sendChallengeCreated(any());
    }

    @Test
    void shouldFallbackToEnglishWhenLangUnknown() {
        Challenge c = new Challenge();
        c.setLanguage("de");
        c.setMsisdn("5550002");

        service.sendChallengeCreated(c);

        verify(enStrategy, times(1)).sendChallengeCreated(c);
        verify(trStrategy, never()).sendChallengeCreated(any());
    }
}
