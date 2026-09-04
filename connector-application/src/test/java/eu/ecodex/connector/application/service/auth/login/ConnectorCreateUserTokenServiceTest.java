/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.auth.login.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorCreateUserTokenServiceTest {

    @Mock
    ConnectorRefreshTokenRepository refreshTokenRepository;

    @Mock
    ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    @Mock
    Clock clock;

    @InjectMocks
    ConnectorRefreshUserTokenService service;


    @Test
    void createOnLogin_should_create_access_and_refresh_token_successfully_and_delete_existing() {
        // Given
        var loginTime = Instant.parse("2026-08-21T10:00:00Z");
        var userId = "uuid";
        var user = ConnectorUser.builder().uuid(userId).build();
        var duration = Duration.ofDays(3);
        var expectedRefreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(loginTime.plus(duration))
            .build();

        when(refreshTokenRepository.deleteByUserUuid(any())).thenReturn(Integer.MAX_VALUE);
        when(authenticationTokenProvider.getRefreshTokenExpiresIn()).thenReturn(duration);
        when(clock.instant()).thenReturn(loginTime);
        when(refreshTokenRepository.save(any())).thenReturn(expectedRefreshToken);

        // When

        var refreshToken = service.create(user);

        // Then
        assertThat(refreshToken).isNotNull();
        verify(refreshTokenRepository).save(expectedRefreshToken);
        verify(refreshTokenRepository).deleteByUserUuid(userId);

        verifyNoMoreInteractions(refreshTokenRepository, authenticationTokenProvider, clock);
    }
}