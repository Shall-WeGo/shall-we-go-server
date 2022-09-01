package com.sam.shallwego.global.jwt.config;

import com.sam.shallwego.global.jwt.AuthenticationConverter;
import com.sam.shallwego.global.jwt.JwtReactiveAuthenticationManager;
import com.sam.shallwego.global.jwt.handler.JwtVerifyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@RequiredArgsConstructor
public class JwtFilterConfig {

    private final JwtSecretConfig jwtSecretConfig;

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter() {
        AuthenticationWebFilter jwtAuthenticationWebFilter;
        ServerAuthenticationConverter tokenConverter;
        ReactiveAuthenticationManager authenticationManager;

        authenticationManager = new JwtReactiveAuthenticationManager();
        jwtAuthenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        tokenConverter = new AuthenticationConverter(
                new JwtVerifyHandler(jwtSecretConfig)
        );

        jwtAuthenticationWebFilter.setServerAuthenticationConverter(tokenConverter);
        jwtAuthenticationWebFilter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers("/**")
        );

        return jwtAuthenticationWebFilter;
    }
}
