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

import eu.ecodex.connector.application.port.api.auth.login.ConnectorRefreshTokenCleanUp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Cleans up expired refresh tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RefreshTokenCleanupScheduler {

    ConnectorRefreshTokenCleanUp connectorRefreshTokenCleanUp;

    /**
     * How long to retain revoked tokens.
     */
    @Scheduled(cron = "${connector.auth.security.jwt.refresh-token.cleanup-cron}")
    public void purgeStaleTokens() {
        connectorRefreshTokenCleanUp.purgeStaleTokens();
    }
}
