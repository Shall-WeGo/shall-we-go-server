package com.sam.shallwego.global.jwt.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sam.shallwego.global.exception.BusinessException;
import com.sam.shallwego.global.jwt.config.JwtSecretConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class JwtVerifyHandler {

    private final JwtSecretConfig jwtSecretConfig;

    public Mono<DecodedJWT> check(String accessToken) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecretConfig.getAccessSecret());
        return Mono.just(JWT.require(algorithm).build().verify(accessToken))
                .onErrorResume(e -> Mono.error(new BusinessException()));
    }
}
