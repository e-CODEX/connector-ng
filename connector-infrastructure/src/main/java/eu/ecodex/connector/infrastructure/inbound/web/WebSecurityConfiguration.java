/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web;

import eu.ecodex.connector.infrastructure.property.ConnectorCorsProperties;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                   .cors(Customizer.withDefaults())
                   .authorizeHttpRequests(request -> request
                           .requestMatchers(
                                   "/api/v1/admin/business-domains",
                                   "/api/v1/admin/processing-modes",
                                   "/api/v1/admin/processing-modes/{uuid}",
                                   "/api/v1/admin/attachments",
                                   "/api/v1/attachments/upload",
                                   "/api/v1/admin/messages",
                                   "/api/v1/admin/messages/stats",
                                   "/api/v1/admin/messages/reports",
                                   "/api/v1/admin/messages/{identifier}",
                                   "/api/v1/admin/messages/{identifier}/transport-steps",
                                   "/api/v1/messages/outbound",
                                   "/api/v1/messages/evidence-trigger",
                                   "/api/v1/admin/transport-steps",
                                   "/api/v1/admin/configurations/business-domains",
                                   "/api/v1/admin/configurations/container",
                                   "/api/v1/admin/configurations/business-document",
                                   "/api/v1/admin/configurations/evidence",
                                   "/api/v1/admin/configurations/routing",
                                   "/api/v1/admin/configurations/backend-link-partners",
                                   "/api/v1/admin/configurations/queues",
                                   "/api/v1/admin/configurations/message-processing",
                                   // SOAP
                                   "/services/backend",
                                   // swagger ui
                                   "/swagger-ui/**",
                                   "/v3/api-docs/**",
                                   "/swagger-resources/**"
                           ).permitAll()
                           .anyRequest().authenticated()
                   )
                   .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(ConnectorCorsProperties corsProperties) {
        var configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        configuration.setMaxAge(corsProperties.getMaxAge());

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
