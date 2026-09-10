/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.token;

import eu.ecodex.connector.application.port.api.auth.token.ConnectorCleanupUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorRefreshTokenRepository;
import java.time.Clock;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Cleans up stale refresh tokens.
 */
@Slf4j
@Component
public class ConnectorCleanupUserRefreshTokenService implements ConnectorCleanupUserRefreshToken {
    private final ConnectorRefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    public ConnectorCleanupUserRefreshTokenService(
        ConnectorRefreshTokenRepository refreshTokenRepository, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.clock = clock;
    }

    /**
     * How long to retain revoked tokens and then purge them.
     */
    @Override
    @Transactional
    public void execute() {
        var now = clock.instant();
        var expiredDeleted = refreshTokenRepository.deleteByExpiryDateBefore(now);
        var revokedDeleted = refreshTokenRepository.deleteByRevokedAndExpiryDateBefore(
            now.minus(Duration.ofDays(30)));
        if (expiredDeleted > 0 || revokedDeleted > 0) {
            log.info("Refresh token cleanup: removed {} expired, {} old revoked tokens",
                expiredDeleted, revokedDeleted);
        }
    }
}
