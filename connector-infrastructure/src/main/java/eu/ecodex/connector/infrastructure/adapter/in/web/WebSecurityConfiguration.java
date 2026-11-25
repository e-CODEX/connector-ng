/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web;

import eu.ecodex.connector.domain.model.user.ConnectorRoleName;
import eu.ecodex.connector.infrastructure.adapter.out.auth.accesstoken.ConnectorJwtAuthenticationFilter;
import eu.ecodex.connector.infrastructure.config.property.ConnectorBasicAuthProperties;
import eu.ecodex.connector.infrastructure.config.property.ConnectorCorsProperties;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configures the web security for the application, defining security filters, CORs settings,
 * password encoding, and authentication management.
 *
 * <p>This configuration sets up:</p>
 * - A custom security filter chain integrating JWT authentication. - Cross-Origin Resource Sharing
 * (CORS) settings based on application properties. - Stateless session management. - Role-based
 * access control and endpoint-specific permissions. - Exception handling for unauthorized and
 * access-denied responses.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {
    private static final String BASIC_AUTH_ROLE = "BASIC_AUTH";

    private final ConnectorJwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfiguration(ConnectorJwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Protects the Swagger UI, the OpenAPI documents and the actuator endpoints with HTTP Basic
     * authentication.
     *
     * <p>This chain only matches the documentation and actuator paths and uses its own in-memory
     * user, so it is independent of the JWT authentication of the REST API. CORS is enabled on it
     * so that a browser application can call the actuator: the CORS configuration source only
     * covers the actuator, not the documentation.
     *
     * @param http                the {@code HttpSecurity} object used to configure the security
     *                            settings
     * @param passwordEncoder     the encoder used to hash the configured password
     * @param basicAuthProperties the Basic authentication credentials
     *
     * @return the {@code SecurityFilterChain} protecting the API documentation and the actuator
     */
    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthFilterChain(
        HttpSecurity http,
        PasswordEncoder passwordEncoder,
        ConnectorBasicAuthProperties basicAuthProperties) {
        var user = User.withUsername(basicAuthProperties.username())
                       .password(passwordEncoder.encode(basicAuthProperties.password()))
                       .roles(BASIC_AUTH_ROLE)
                       .build();

        return http
            .securityMatcher(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/actuator/**"
            )
            .csrf(AbstractHttpConfigurer::disable)
            // answers the pre-flight requests of the actuator before the authentication
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                                   session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .userDetailsService(new InMemoryUserDetailsManager(user))
            .authorizeHttpRequests(request -> request.anyRequest().hasRole(BASIC_AUTH_ROLE))
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    /**
     * Configures the security filter chain for the application by defining HTTP security rules.
     *
     * @param http the {@code HttpSecurity} object used to configure the security settings
     *
     * @return the {@code SecurityFilterChain} object representing the configured security filter
     *     chain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .httpBasic(AbstractHttpConfigurer::disable)
                   .cors(Customizer.withDefaults())
                   .authorizeHttpRequests(request -> request
                       // Allow internal ERROR (400, 404, 405, 415 ,500) dispatch
                       // (forward to /error) without authentication. Without this, Spring
                       // Security 6+ also applies authorization to that dispatch.
                       // As it has no user the request is rejected with 403
                       .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                       // the message statistics and reports and the list of the business domains
                       // are also open to the other roles
                       .requestMatchers(
                           HttpMethod.GET,
                           "/api/v1/admin/messages/stats",
                           "/api/v1/admin/messages/reports",
                           "/api/v1/admin/business-domains"
                       )
                       .authenticated()
                       // the administration API is for administrators: an endpoint under this
                       // path is closed to the other roles unless it is opened above
                       .requestMatchers("/api/v1/admin/**")
                       .hasRole(ConnectorRoleName.ADMIN.name())
                       // All authenticated users
                       .requestMatchers("/api/v1/auth/me")
                       .authenticated()
                       .requestMatchers(
                           "/api/v1/attachments/upload",
                           "/api/v1/messages/outbound",
                           "/api/v1/messages/evidence-trigger",
                           "/api/v1/services",
                           "/api/v1/processing-modes/{identifier}/services",
                           "/api/v1/processing-modes/{identifier}/actions",
                           "/api/v1/processing-modes/{identifier}/parties",
                           "/api/v1/link-partners",
                           "/api/v1/auth/login",
                           "/api/v1/auth/refresh",
                           // SOAP
                           "/services/backend"
                       )
                       .permitAll()
                       .anyRequest()
                       .authenticated()
                   )
                   .addFilterBefore(
                       jwtAuthenticationFilter,
                       UsernamePasswordAuthenticationFilter.class
                   )
                   .sessionManagement(session -> session.sessionCreationPolicy(
                       SessionCreationPolicy.STATELESS))
                   .exceptionHandling(exceptionHandling -> exceptionHandling
                       .authenticationEntryPoint((req, resp, ex) -> resp.sendError(
                           HttpServletResponse.SC_UNAUTHORIZED))
                       .accessDeniedHandler((req, resp, ex) -> resp.sendError(
                           HttpServletResponse.SC_FORBIDDEN)))
                   .build();
    }

    /**
     * Creates the CORS configuration source applied to the REST API and to the actuator of the
     * connector.
     *
     * <p>The configuration is built from {@link ConnectorCorsProperties} (allowed origins,
     * methods, headers, credentials and pre-flight cache duration) and registered for the REST API
     * ({@code /api/**}) and the actuator ({@code /actuator/**}) only: the Swagger documentation and
     * the SOAP services are not meant to be called from a browser application of another origin.
     * Spring Security picks up this bean automatically when CORS is enabled on the
     * {@code HttpSecurity} filter chain.
     *
     * @param corsProperties the externalized CORS settings
     *
     * @return the {@link CorsConfigurationSource} used to resolve the CORS configuration of each
     *     request
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(ConnectorCorsProperties corsProperties) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(corsProperties.allowedMethods());
        configuration.setAllowedHeaders(corsProperties.allowedHeaders());
        configuration.setAllowCredentials(corsProperties.allowCredentials());
        configuration.setMaxAge(corsProperties.maxAge());

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/actuator/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }
}
