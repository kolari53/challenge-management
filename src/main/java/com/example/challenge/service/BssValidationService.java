package com.example.challenge.service;

import org.springframework.stereotype.Service;

@Service
public class BssValidationService {

    public boolean isRegistered(String msisdn) {
        return msisdn != null && msisdn.startsWith("53");
    }

    public boolean isActive(String msisdn) {
        if (msisdn == null || msisdn.isEmpty()) return false;
        char last = msisdn.charAt(msisdn.length() - 1);
        return Character.isDigit(last) && ((last - '0') % 2 == 0);
    }
}
