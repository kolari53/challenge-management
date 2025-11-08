package com.example.challenge.controller;

import com.example.challenge.domain.ChallengeStatus;
import com.example.challenge.service.ChallengeService;
import com.example.challenge.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/csr")
@RequiredArgsConstructor
public class CSRController {

    private final ChallengeService service;
    private final AuditService audit;

    @PostMapping("/accept")
    public ResponseEntity<String> accept(@RequestParam String msisdn,
                                         @RequestParam String csrUserId) {
        service.reply(msisdn, "YES");
        audit.log(Long.valueOf(msisdn), ChallengeStatus.CSR_ACCEPT, csrUserId, "CSR accepted the challenge");
        return ResponseEntity.ok("Challenge manually accepted by CSR");
    }

    @PostMapping("/reject")
    public ResponseEntity<String> reject(@RequestParam String msisdn,
                                         @RequestParam String csrUserId) {
        service.reply(msisdn, "NO");
        audit.log(Long.valueOf(msisdn), ChallengeStatus.CSR_REJECT, csrUserId, "CSR rejected the challenge");
        return ResponseEntity.ok("Challenge manually rejected by CSR");
    }
}
