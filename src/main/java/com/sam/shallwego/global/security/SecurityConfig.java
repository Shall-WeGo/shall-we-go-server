package com.sam.shallwego.global.security;

import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.global.jwt.JwtUtil;
import com.sam.shallwego.global.jwt.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final ApplicationContext applicationContext;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @DependsOn({"methodSecurityExpressionHandler"})
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtUtil jwtUtil,
                                                         ReactiveAuthenticationManager manager) {
        DefaultMethodSecurityExpressionHandler defaultExpressionHandler
                = applicationContext.getBean(DefaultMethodSecurityExpressionHandler.class);
        defaultExpressionHandler.setPermissionEvaluator(permissionEvaluator());
        return http
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec.authenticationEntryPoint((exchange, ex) -> {
                        return Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        });
                    }).accessDeniedHandler((exchange, denied) -> {
                        return Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        });
                    });
                }).csrf().disable()
                .formLogin().disable()
                .cors().disable()
                .httpBasic().disable()
                .authenticationManager(manager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(HttpMethod.OPTIONS).permitAll()
                            .pathMatchers("/actuator/**").permitAll()
                            .pathMatchers(HttpMethod.POST,"/users/**").permitAll()
                            .pathMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                            .anyExchange().authenticated();
                }).addFilterAt(new JwtFilter(jwtUtil), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return new PermissionEvaluator() {
            @Override
            public boolean hasPermission(Authentication authentication,
                                         Object targetDomainObject, Object permission) {
                return (authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                .equals(targetDomainObject)));
            }

            @Override
            public boolean hasPermission(Authentication authentication,
                                         Serializable targetId, String targetType, Object permission) {
                return false;
            }
        };
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(MemberRepository memberRepository) {
        return username -> Mono.fromCallable(() -> memberRepository.findByUsername(username)
                .orElseThrow(Member.NotExistsException::new))
                .cast(UserDetails.class)
                .subscribeOn(Schedulers.boundedElastic())
                .log();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService detailsService,
            PasswordEncoder passwordEncoder) {
        var authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(detailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }
}
