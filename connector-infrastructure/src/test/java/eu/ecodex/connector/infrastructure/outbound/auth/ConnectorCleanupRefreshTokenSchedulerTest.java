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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import eu.ecodex.connector.application.port.api.auth.token.ConnectorCleanupUserRefreshToken;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorCleanupRefreshTokenScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorCleanupRefreshTokenSchedulerTest {

    @Mock
    ConnectorCleanupUserRefreshToken refreshTokenCleanUp;

    @InjectMocks
    ConnectorCleanupRefreshTokenScheduler scheduler;

    @Test
    void purgeStaleTokens_should_remove_stale_refresh_tokens() {
        // Given
        doNothing().when(refreshTokenCleanUp).execute();
        // When
        scheduler.purgeStaleTokens();

        // Then
        verify(refreshTokenCleanUp).execute();
    }
}