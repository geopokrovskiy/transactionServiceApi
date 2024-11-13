package com.geopokrovskiy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final String[] publicRoutes = {"/api/v1/wallet_types/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.OPTIONS).permitAll() // Allow OPTIONS requests for CORS preflight
                                .requestMatchers(publicRoutes).permitAll()       // Allow access to public routes
                                .anyRequest().authenticated()
                )
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .build();
    }

    // Entry point handler for unauthorized access
    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            log.error("Unauthorized error: {}", authException.getMessage());
            response.setStatus(401);
        };
    }

    // Access Denied handler
    private AccessDeniedHandlerImpl accessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage("/403");
        return handler;
    }
}