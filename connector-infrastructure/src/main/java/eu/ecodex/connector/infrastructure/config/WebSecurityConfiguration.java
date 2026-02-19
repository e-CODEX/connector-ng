/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures the security settings for the application. The security settings are configured using
 * Spring Security's {@code SecurityFilterChain}.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    /**
     * Configures the security filter chain for the application by defining HTTP security rules.
     *
     * @param http the {@code HttpSecurity} object used to configure the security settings
     *
     * @return the {@code SecurityFilterChain} object representing the configured security filter
     *         chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .httpBasic(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(request -> request
                           .requestMatchers(
                                   "/api/v1/admin/business-domains",
                                   "/api/v1/admin/processing-modes",
                                   "/api/v1/attachments/upload",
                                   // swagger ui
                                   "/swagger-ui/**",
                                   "/v3/api-docs/**",
                                   "/swagger-resources/**"
                           ).permitAll()
                           .anyRequest().authenticated()
                   )
                   .build();
    }
}
