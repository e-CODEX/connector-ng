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

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.port.api.auth.login.ConnectorRevokeUserToken;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * A service implementation responsible for revoking user refresh tokens in the Connector system.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRevokeUserTokenService implements ConnectorRevokeUserToken {

    ConnectorRefreshTokenRepository repository;

    @Override
    public void revoke(String userId, String token) {
        if (token == null || token.isBlank()) {
            throw new ConnectorUserBadCredentialsException("Invalid token");
        }

        var refreshToken = repository.findByToken(token)
            .orElseThrow(() ->
                new ConnectorUserBadCredentialsException("Invalid refresh token"));

        if (!refreshToken.user().uuid().equals(userId)) {
            throw new ConnectorUserBadCredentialsException(
                "Invalid refresh token for user " + userId);
        }

        log.info("Revoking refresh token {}", token);
        if (refreshToken.revoked()) {
            return;
        }

        repository.save(refreshToken.toBuilder().revoked(true).build());
    }

}
