package com.example.challenge.state;

import com.example.challenge.domain.Challenge;
import org.springframework.stereotype.Component;

@Component("PENDING")
public class PendingState implements ChallengeState {

    @Override
    public void handle(Challenge challenge) {
        System.out.println("PENDING state: challenge created and waiting for customer reply...");
    }

    @Override
    public String getName() {
        return "PENDING";
    }
}
