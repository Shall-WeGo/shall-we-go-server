package com.sam.shallwego.global.jwt;

import com.sam.shallwego.domain.member.service.AuthService;
import com.sam.shallwego.global.exception.BusinessException;
import com.sam.shallwego.global.jwt.config.JwtSecretConfig;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";
    private static final long ACCESS_TIME = 1000 * 60 * 60;

    private final JwtSecretConfig jwtSecretConfig;
    private final AuthService authService;

    public String generateAccessToken(String memberId) {
        return generateToken(ACCESS, memberId, ACCESS_TIME);
    }

    public String generateRefreshToken(String memberId) {
        return generateToken(REFRESH, memberId, ACCESS_TIME * 24);
    }

    private Claims parseToken(String token, String type) {
        return Jwts.parser()
                .setSigningKey(getSecretFromType(type))
                .parseClaimsJws(token)
                .getBody();
    }

    private String getSecretFromType(String type) {
        return (type.equalsIgnoreCase(ACCESS))
                ? jwtSecretConfig.getAccessSecret()
                : jwtSecretConfig.getRefreshSecret();
    }

    public String extractIdFromToken(String token, String type) {
        try {
            return parseToken(token, type).getSubject();
        } catch (SignatureException | MalformedJwtException e) {
            throw new TokenException("잘못된 Jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new TokenException("만료된 토큰입니다.");
        } catch (IllegalArgumentException | UnsupportedJwtException e) {
            throw new TokenException("비정상적인 토큰입니다.");
        }
    }

    private String generateToken(String type, String memberId, long expWithMs) {
        final Date tokenCreateDate = new Date();

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, getSecretFromType(type))
                .setSubject(memberId)
                .claim("type", type)
                .setIssuedAt(tokenCreateDate)
                .setExpiration(new Date(tokenCreateDate.getTime() + (expWithMs * 1000)))
                .compact();
    }

    public Authentication getAuthenticationFromToken(String token) {
        UserDetails authentication
                = authService.findByUsername(extractIdFromToken(token, ACCESS))
                .block();
        return new UsernamePasswordAuthenticationToken(
                authentication, ""
        );
    }

    public static class TokenException extends BusinessException {
        public TokenException(String message) {
            super(HttpStatus.UNAUTHORIZED, message);
        }
    }
}
