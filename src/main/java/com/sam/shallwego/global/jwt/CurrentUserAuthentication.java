package com.sam.shallwego.global.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CurrentUserAuthentication extends AbstractAuthenticationToken {

    private final String tokenType;
    private final Long userId;
    private final DecodedJWT decodedJWT;

    public CurrentUserAuthentication(String tokenType, Long userId,
                                     DecodedJWT decodedJWT) {
        super(null);
        this.tokenType = tokenType;
        this.userId = userId;
        this.decodedJWT = decodedJWT;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return decodedJWT;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
