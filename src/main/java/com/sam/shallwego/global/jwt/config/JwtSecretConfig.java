package com.sam.shallwego.global.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtSecretConfig {
    private String accessSecret;
    private String refreshSecret;
}
