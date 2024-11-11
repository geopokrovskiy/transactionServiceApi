package com.geopokrovskiy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebSecurityConfig {

    private final String[] publicRoutes = {"/api/v1/wallet_types/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .pathMatchers(publicRoutes)
                .permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    log.error("IN securityWebFilterChain - unauthorized error: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                .accessDeniedHandler((swe, e) -> {
                    log.error("IN securityWebFilterChain - access denied: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                })
                .and()
                .build();
    }

}
