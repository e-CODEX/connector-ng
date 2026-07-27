/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.ConnectorUserTestFixtures;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtService jwtService;

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_without_AuthorizationHeader_continuesChainWithoutAuthenticating()
        throws Exception {
        // Given
        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isEqualTo(request);

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void doFilterInternal_withNonBearerHeader_continuesChainWithoutAuthenticating()
        throws Exception {
        // Given
        var token = "basic-token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + token);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isEqualTo(request);

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void doFilterInternal_withInvalidToken_doesNotSetAuthentication() throws Exception {
        // Given
        var token = "expired-or-tampered-token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        var userDetails = ConnectorUserTestFixtures.createUserDetails();

        when(jwtService.extractUsername(any())).thenReturn(userDetails.getUsername());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtService.isValidToken(token, userDetails)).thenReturn(false);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isNull();

        verifyNoMoreInteractions(jwtService, userDetailsService);
    }

    @Test
    void doFilterInternal_withMalformedToken_clearsContextAndContinuesChain() throws Exception {
        // Given
        var token = "not-a-real-jwt";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(jwtService.extractUsername(any())).thenThrow(new MalformedJwtException("bad token"));

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isNull();

        verifyNoMoreInteractions(jwtService, userDetailsService);
    }


    @Test
    void doFilterInternal_withValidToken_setsAuthentication() throws Exception {
        // Given
        var token = "valid-token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        var userDetails = ConnectorUserTestFixtures.createUserDetails();

        when(jwtService.extractUsername(token)).thenReturn("john.doe");
        when(userDetailsService.loadUserByUsername("john.doe")).thenReturn(userDetails);
        when(jwtService.isValidToken(token, userDetails)).thenReturn(true);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).extracting("authority")
            .containsExactly("ROLE_ADMIN");
        assertThat(filterChain.getRequest()).isEqualTo(request);

        verifyNoMoreInteractions(jwtService, userDetailsService);
    }
}