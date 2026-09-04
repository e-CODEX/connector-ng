/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import eu.ecodex.connector.TestConfiguration;
import eu.ecodex.connector.infrastructure.outbound.auth.JwtAuthenticationFilter;
import eu.ecodex.connector.infrastructure.outbound.auth.JwtService;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Base abstract class for testing Spring MVC controllers.
 * Provides basic configuration and utilities for testing controllers
 * annotated with @WebMvcTest. It simplifies the setup and mocking
 * necessary for performing unit tests on Web MVC REST endpoints.
 *
 * <p>Mocks the components necessary for JWT token processing (e.g., JwtTokenService and
 * JwtAuthenticationFilter).
 *
 * <p>Intercepts and mocks the behavior of the JwtAuthenticationFilter to allow seamless processing
 * of HTTP requests through the filter chain during testing.
 */
@AutoConfigureRestTestClient
@ContextConfiguration(classes = TestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class AbstractWebMvcTest {
    @MockitoBean
    protected JwtService jwtTokenService;

    @MockitoBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the mocked JWT authentication filter as a transparent pass-through filter.
     *
     * <p>Controller slice tests should focus on MVC concerns such as request mapping,
     * validation, serialization, controller delegation, and exception handling. They should not
     * depend on the real security filter chain. For that reason, servlet filters are disabled with
     * {@code @AutoConfigureMockMvc(addFilters = false)}.
     *
     * <p>The {@link JwtAuthenticationFilter} bean is still mocked because some MVC test contexts
     * may need it during Spring context creation, especially when security configuration is
     * discovered.
     *
     * <p>This setup makes the mocked filter safe if it is invoked accidentally: it simply
     * continues the filter chain and lets the controller produce the real response status.
     */
    @BeforeEach
    void configureJwtAuthenticationFilterMock() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        })
            .when(jwtAuthenticationFilter)
            .doFilter(any(), any(), any());
    }

    protected static RequestPostProcessor authenticatedAs(ConnectorUserDetails principal) {
        return (MockHttpServletRequest request) -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
            );
            SecurityContextHolder.setContext(context);
            return request;
        };
    }

}
