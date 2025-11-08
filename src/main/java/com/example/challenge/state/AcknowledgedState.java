package com.example.challenge.state;

import com.example.challenge.domain.Challenge;
import org.springframework.stereotype.Component;

@Component("ACKNOWLEDGED")
public class AcknowledgedState implements ChallengeState {

    @Override
    public void handle(Challenge challenge) {
        System.out.println("ACKNOWLEDGED state: awaiting customer confirmation...");
    }

    @Override
    public String getName() {
        return "ACKNOWLEDGED";
    }
}
