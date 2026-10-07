/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.api.auth.ConnectorLoginUser;
import eu.ecodex.connector.application.port.api.auth.ConnectorLogoutUser;
import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordAtFirstLogin;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorRefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorAuthenticationController.class)
class ConnectorRefreshTokenControllerTest extends AbstractWebMvcTest {
    @MockitoBean
    ConnectorUpdateUserPasswordAtFirstLogin updateUserPasswordAtFirstLogin;
    @MockitoBean
    ConnectorLoginUser loginUser;
    @MockitoBean
    ConnectorLogoutUser logoutUser;
    @MockitoBean
    ConnectorRefreshUserRefreshToken userRefreshToken;

    @Autowired
    RestTestClient apiClient;

    @Test
    void refresh_should_refresh_the_token() {
        // Given
        var accessToken = "access-token";
        var refreshToken = "refresh-token";
        var request = ConnectorRefreshTokenRequest.builder()
            .refreshToken(refreshToken)
            .build();
        var expected = ConnectorUserAuthenticationResult.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();

        when(userRefreshToken.execute(any(), any())).thenReturn(expected);

        // When
        var result = apiClient.post()
            .uri("/api/v1/auth/refresh")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ConnectorUserAuthenticationResult.class);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResponseBody()).isNotNull();
        assertThat(result.getResponseBody()).isEqualTo(expected);

        verify(userRefreshToken).execute(accessToken, refreshToken);
        verifyNoMoreInteractions(loginUser, logoutUser, userRefreshToken,
            updateUserPasswordAtFirstLogin);
    }
}
