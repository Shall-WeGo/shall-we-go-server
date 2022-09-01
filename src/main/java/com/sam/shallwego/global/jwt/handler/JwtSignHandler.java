package com.sam.shallwego.global.jwt.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sam.shallwego.global.jwt.config.JwtSecretConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtSignHandler {

    private static final Long ACCESS_EXPIRE = 1000 * 60L * 60L;
    private static final Long REFRESH_EXPIRE = ACCESS_EXPIRE * 24;
    private final JwtSecretConfig jwtSecretConfig;

    public String createAccessToken(String type0, Long userId) {
        Algorithm algorithm = Algorithm.HMAC256(getSecret(type0));
        return JWT.create()
                .withClaim("type", type0)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(getExpires(type0))
                .sign(algorithm);
    }

    private String getSecret(String type) {
        return (isAccess(type))
                ? jwtSecretConfig.getAccessSecret()
                : jwtSecretConfig.getRefreshSecret();
    }

    private Date getExpires(String type) {
        Date date = new Date();
        return new Date(date.getTime() + (isAccess(type)
                ? ACCESS_EXPIRE
                : REFRESH_EXPIRE
        ));
    }

    private boolean isAccess(String type) {
        return type.equals("accessToken");
    }
}
