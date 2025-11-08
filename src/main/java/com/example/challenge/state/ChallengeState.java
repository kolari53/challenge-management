package com.example.challenge.state;

import com.example.challenge.domain.Challenge;

public interface ChallengeState {
    void handle(Challenge challenge);
    String getName();
}
