package com.example.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI challengeApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Challenge Management API")
                .version("1.0.0")
                .description("Port-Out Challenge yönetim sistemi için REST API dokümantasyonu"));
    }
}
