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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorLoginUserServiceTest {
    @Mock
    ConnectorUserAuthenticationProvider userAuthenticationProvider;

    @InjectMocks
    ConnectorLoginUserService connectorLoginUserService;

    @Test
    void execute_should_return_login_result() {
        // Given
        var username = "username";
        var password = "password";
        var expected = ConnectorUserAuthenticationResult.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .expiresIn(Duration.ofMinutes(15).getSeconds())
            .refreshExpiresIn(Duration.ofDays(30).getSeconds())
            .build();
        when(userAuthenticationProvider.login(any(), any())).thenReturn(expected);

        // When
        var result = connectorLoginUserService.execute(username, password);

        // Then
        assertThat(result).isNotNull().usingRecursiveComparison().isEqualTo(expected);
        verify(userAuthenticationProvider).login(username, password);
        verifyNoMoreInteractions(userAuthenticationProvider);
    }
}