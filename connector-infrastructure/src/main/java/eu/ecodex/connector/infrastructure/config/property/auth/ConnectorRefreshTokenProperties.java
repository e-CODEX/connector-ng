/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config.property.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Refresh token configuration properties.
 *
 * @param expiration  how long a refresh token remains valid
 * @param cleanupCron cron expression controlling when expired refresh tokens are purged
 * @param reuseGrace  how long after its replacement a revoked refresh token may still be
 *                    presented without being considered as stolen
 */
public record ConnectorRefreshTokenProperties(
    @NotNull Duration expiration,
    @NotBlank String cleanupCron,
    @NotNull @DefaultValue("10s") Duration reuseGrace
) {
}
