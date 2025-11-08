package com.example.challenge.domain;

public enum ChallengeStatus {
    ACKNOWLEDGED,   // BSS kontrolleri geçti
    PENDING,        // SMS gönderildi, müşteri cevabı bekleniyor
    ACCEPTED,       // Müşteri/CSR onayladı
    REJECTED,       // Müşteri/CSR reddetti
    CANCELLED,      // Portability tarafından iptal edildi
    EXPIRED,        // Süresi doldu
    COMPLETED,      // Challenge süreci tamamlandı
    INVALID_REPLY,
    CREATED,
    UPDATED,
    CSR_MANUAL_REPLY,
    CSR_ACCEPT,
    CSR_REJECT
}
