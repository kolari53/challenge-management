package com.example.challenge.state;

import com.example.challenge.domain.Challenge;
import org.springframework.stereotype.Component;

@Component("TERMINAL")
public class TerminalState implements ChallengeState {

    @Override
    public void handle(Challenge challenge) {
        System.out.println("TERMINAL state reached for msisdn: " + challenge.getMsisdn());
    }

    @Override
    public String getName() {
        return "TERMINAL";
    }
}
