package com.sam.shallwego.global.jwt;

import com.sam.shallwego.global.jwt.handler.JwtVerifyHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
public class AuthenticationConverter
        implements ServerAuthenticationConverter {

    private final JwtVerifyHandler jwtVerifyHandler;

    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength
            = authValue -> authValue.length() > BEARER.length();
    private static final Function<String, Mono<String>> isolateBearerValue
            = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(AuthorizationHeaderPayload::extractFrom)
                .filter(matchBearerLength)
                .flatMap(isolateBearerValue)
                .flatMap(jwtVerifyHandler::check)
                .flatMap(JwtProvider::create);
    }
}
