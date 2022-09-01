package com.sam.shallwego.global.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtProvider {

    public static Mono<Authentication> create(DecodedJWT decodedJWT) {
        String type;
        Long userId;

        try {
            type = decodedJWT.getClaims().get("type").asString();
            userId = decodedJWT.getClaims().get("userId").asLong();
        } catch (Exception e) {
            log.error("json web token parse error. invalid claims decoded JWT: {}",
                    decodedJWT.getClaims());
            return Mono.empty();
        }

        return Mono.justOrEmpty(new CurrentUserAuthentication(type, userId, decodedJWT));
    }
}
