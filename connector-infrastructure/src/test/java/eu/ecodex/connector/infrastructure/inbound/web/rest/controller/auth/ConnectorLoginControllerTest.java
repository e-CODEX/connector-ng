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

import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRefreshUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorLoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ConnectorAuthenticationController.class)
class ConnectorLoginControllerTest extends AbstractWebMvcTest {
    @MockitoBean
    ConnectorUserAuthenticationProvider userAuthenticationProvider;
    @MockitoBean
    ConnectorUpdateUserPassword updateUserPassword;
    @MockitoBean
    ConnectorRefreshUserRefreshToken userRefreshToken;

    @Autowired
    RestTestClient apiClient;

    @Test
    void login_should_return_200() {
        // Given
        var username = "username";
        var password = "pwd";
        var request = ConnectorLoginRequest.builder().username(username).password(password).build();
        var expected = ConnectorUserAuthenticationResult.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .build();

        when(userAuthenticationProvider.login(any(), any())).thenReturn(expected);

        // When
        var result = apiClient.post()
            .uri("/api/v1/auth/login")
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

        verify(userAuthenticationProvider).login(username, password);
        verifyNoMoreInteractions(userAuthenticationProvider, userRefreshToken, updateUserPassword);
    }
}
