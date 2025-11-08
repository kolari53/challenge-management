package com.example.challenge.controller;

import com.example.challenge.domain.Challenge;
import com.example.challenge.service.ChallengeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChallengeService service;

    @Test
    void createChallengeShouldReturn200() throws Exception {
        Challenge c = new Challenge();
        c.setMsisdn("5550001");

        Mockito.when(service.createChallenge(anyString(), anyString(), anyString()))
            .thenReturn(c);

        mockMvc.perform(post("/api/challenge/create")
                .param("msisdn", "5550001")
                .param("account", "ACC01")
                .param("lang", "tr"))
            .andExpect(status().isOk());
    }

    @Test
    void cancelChallengeShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/challenge/cancel")
                .param("msisdn", "5550001"))
            .andExpect(status().isOk());
    }

    @Test
    void replyChallengeShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/challenge/reply")
                .param("msisdn", "5550001")
                .param("reply", "YES"))
            .andExpect(status().isOk());
    }
}
