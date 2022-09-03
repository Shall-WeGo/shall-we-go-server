package com.sam.shallwego.global.jwt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtSecretConfig {
    private String accessSecret;
    private String refreshSecret;
}
