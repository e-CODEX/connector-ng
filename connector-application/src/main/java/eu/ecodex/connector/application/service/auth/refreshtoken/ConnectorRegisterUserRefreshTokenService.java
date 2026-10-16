/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.refreshtoken;

import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorIssueUserRefreshToken;
import eu.ecodex.connector.application.port.api.auth.refreshtoken.ConnectorRegisterUserRefreshToken;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for creating user authentication  refresh tokens.
 * This service handles the creation of {@link ConnectorRefreshToken} objects.
 *
 * <p>Dependencies:
 * - {@link ConnectorRefreshTokenRepository}: Used for CRUD operations on refresh tokens.
 * - {@link ConnectorAuthenticationTokenProvider}: Provides mechanisms for generating new access
 * tokens.
 */
@Slf4j
@Service
@Transactional
public class ConnectorRegisterUserRefreshTokenService implements ConnectorRegisterUserRefreshToken {
    private final ConnectorRefreshTokenRepository repository;
    private final ConnectorIssueUserRefreshToken issueUserRefreshToken;

    /**
     * Constructs a new instance of {@link ConnectorRegisterUserRefreshTokenService}.
     *
     * @param repository            The repository for managing refresh tokens.
     * @param issueUserRefreshToken The service responsible for issuing new refresh token
     */
    public ConnectorRegisterUserRefreshTokenService(
        ConnectorRefreshTokenRepository repository,
        ConnectorIssueUserRefreshToken issueUserRefreshToken) {
        this.repository = repository;
        this.issueUserRefreshToken = issueUserRefreshToken;
    }

    @Override
    public ConnectorRefreshToken execute(@NonNull ConnectorUser user) {
        log.debug("Creating refresh token for user {}", user.uuid());

        // Delete all existing refresh tokens for the current user
        repository.deleteByUserUuid(user.uuid());

        return issueUserRefreshToken.execute(user);
    }
}
