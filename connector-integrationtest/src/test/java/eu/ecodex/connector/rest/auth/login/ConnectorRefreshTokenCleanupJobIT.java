/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.rest.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.application.service.auth.login.ConnectorRefreshTokenCleanupJob;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

public class ConnectorRefreshTokenCleanupJobIT extends AbstractIntegrationTest {
    @Autowired
    private ConnectorRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ConnectorRefreshTokenCleanupJob refreshTokenCleanupJob;

    @MockitoBean
    private Clock clock;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }


    @Test
    @Sql("classpath:sql/user.sql")
    void should_clean_revoked_refresh_tokens() {

        var userUuid = "d43bfa931-3c25-47e4-b377-bf4ce7b0d04c_default_admin";
        var revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        var notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).hasSize(3);
        assertThat(notRevoked).hasSize(1);

        var instant = Instant.parse("2026-08-18T10:00:00Z");
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        refreshTokenCleanupJob.purgeStaleTokens();

        revoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, true);
        notRevoked = refreshTokenRepository.findByUserUuidAndRevoked(userUuid, false);
        assertThat(revoked).hasSize(0);
        assertThat(notRevoked).hasSize(1);
    }
}
