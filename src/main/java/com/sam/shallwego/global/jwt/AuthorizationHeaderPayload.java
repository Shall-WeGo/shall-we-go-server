package com.sam.shallwego.global.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthorizationHeaderPayload {

    public static Mono<String> extractFrom(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION));
    }
}
