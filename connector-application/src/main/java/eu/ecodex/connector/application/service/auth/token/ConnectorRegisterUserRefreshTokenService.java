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

import eu.ecodex.connector.application.port.api.auth.token.ConnectorRegisterUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.token.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service implementation for creating user authentication tokens.
 * This service handles the creation of {@link ConnectorRefreshToken} objects.
 *
 * <p>Dependencies:
 * - {@link ConnectorRefreshTokenRepository}: Used for CRUD operations on refresh tokens.
 * - {@link ConnectorAuthenticationTokenProvider}: Provides mechanisms for generating new access
 * tokens.
 */
@Slf4j
@Service
public class ConnectorRegisterUserRefreshTokenService implements ConnectorRegisterUserRefreshToken {
    private final ConnectorRefreshTokenRepository repository;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    private final Clock clock;

    /**
     * Constructs a new instance of {@link ConnectorRegisterUserRefreshTokenService}.
     *
     * @param repository The repository for managing refresh tokens.
     * @param authenticationTokenProvider The provider for generating access tokens.
     * @param clock The clock used for time-related operations.
     */
    public ConnectorRegisterUserRefreshTokenService(
        ConnectorRefreshTokenRepository repository,
        ConnectorAuthenticationTokenProvider authenticationTokenProvider, Clock clock) {
        this.repository = repository;
        this.authenticationTokenProvider = authenticationTokenProvider;
        this.clock = clock;
    }

    @Override
    public ConnectorRefreshToken execute(@NonNull ConnectorUser user) {
        log.debug("Creating refresh token for user {}", user.uuid());

        // Delete all existing refresh tokens for the user
        repository.deleteByUserUuid(user.uuid());

        return saveRefreshToken(user);
    }

    /**
     * Creates a new refresh token for the given user.
     *
     * @param user user to create the refresh token for
     *
     * @return newly created refresh token
     */
    private ConnectorRefreshToken saveRefreshToken(ConnectorUser user) {
        var refreshToken = ConnectorRefreshToken.builder()
            .revoked(false)
            .user(user)
            .expiresAt(clock
                .instant()
                .plus(authenticationTokenProvider.getRefreshTokenExpiresIn()))
            .build();

        return repository.save(refreshToken);
    }
}
