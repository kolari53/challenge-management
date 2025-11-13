package com.example.challenge.controller;

import com.example.challenge.domain.Challenge;
import com.example.challenge.domain.ChallengeHistory;
import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.service.AuditService;
import com.example.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@Tag(name = "Challenge Management", description = "Port-Out Challenge API")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;
    private final AuditService auditService;

    @Operation(summary = "Yeni challenge oluştur")
    @PostMapping("/create")
    public ResponseEntity<Challenge> create(
        @RequestParam String msisdn,
        @RequestParam String account,
        @RequestParam(defaultValue = "tr") String lang) throws InterruptedException {
        return ResponseEntity.ok(service.createChallenge(msisdn, account, lang));
    }

    @Operation(summary = "Müşteri yanıtını işle (YES/NO)")
    @PostMapping("/reply")
    public ResponseEntity<String> reply(
        @RequestParam String msisdn,
        @RequestParam String reply) {
        service.reply(msisdn, reply);
        return ResponseEntity.ok("Reply processed successfully");
    }

    @Operation(summary = "Challenge iptali")
    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestParam String msisdn) {
        service.cancel(msisdn);
        return ResponseEntity.ok("Challenge cancelled successfully");
    }
    @PostMapping("/manualReply")
    public ResponseEntity<String> manualReply(
            @RequestParam String msisdn,
            @RequestParam String reply,
            @RequestParam String csrUserId) {

        var challenge = service.reply(msisdn, reply);

        auditService.log(
                challenge.getId(),
                ChallengeStatus.CSR_MANUAL_REPLY,
                csrUserId,
                "CSR manually replied '" + reply + "' for MSISDN " + msisdn
        );

        return ResponseEntity.ok("Manual reply processed successfully");
    }

    @GetMapping("/history/{challengeId}")
    public ResponseEntity<List<ChallengeHistory>> getHistory(@PathVariable Long challengeId) {
        return ResponseEntity.ok(auditService.getHistory(challengeId));
    }
}
