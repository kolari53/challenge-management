package com.example.challenge.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String msisdn;
    private String account;
    private String language;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private LocalDateTime expiresAt;

    public String getPreferredLanguage() {
        return language != null ? language : "en";
    }
}
