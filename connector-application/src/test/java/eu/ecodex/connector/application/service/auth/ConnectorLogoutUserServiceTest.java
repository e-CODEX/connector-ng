/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorLogoutUserServiceTest {
    @Mock
    ConnectorUserAuthenticationProvider userAuthenticationProvider;

    @InjectMocks
    ConnectorLogoutUserService connectorLogoutUserService;

    @Test
    void execute_should_logout_user() {
        // Given
        var uuid = "username";
        var refreshToken = "refresh-token";

        doNothing().when(userAuthenticationProvider).logout(any(), any());

        // When
        connectorLogoutUserService.execute(uuid, refreshToken);

        // Then
        verify(userAuthenticationProvider).logout(uuid, refreshToken);
        verifyNoMoreInteractions(userAuthenticationProvider);
    }
}