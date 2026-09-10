/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.login;

import eu.ecodex.connector.application.port.api.auth.token.ConnectorCleanupUserRefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A scheduled task responsible for cleaning up stale refresh tokens in the system.
 *
 * <p>The {@code ConnectorCleanupRefreshTokenScheduler} class defines a scheduled method
 * that performs cleanup operations for revoked or outdated refresh tokens.
 *
 * <p>Key responsibilities include:
 * - Scheduling and executing the token cleanup operation.
 * - Delegating the token cleanup logic to a {@code ConnectorCleanupUserToken} implementation.
 *
 * <p>The scheduling behavior is controlled via a cron expression that can be
 * configured externally.
 */
@Slf4j
@Component
public class ConnectorCleanupRefreshTokenScheduler {
    private final ConnectorCleanupUserRefreshToken connectorCleanupUserRefreshToken;

    public ConnectorCleanupRefreshTokenScheduler(
        ConnectorCleanupUserRefreshToken connectorCleanupUserRefreshToken) {
        this.connectorCleanupUserRefreshToken = connectorCleanupUserRefreshToken;
    }

    /**
     * How long to retain revoked tokens.
     */
    @Scheduled(cron = "${connector.auth.security.jwt.refresh-token.cleanup-cron}")
    public void purgeStaleTokens() {
        connectorCleanupUserRefreshToken.execute();
    }
}
