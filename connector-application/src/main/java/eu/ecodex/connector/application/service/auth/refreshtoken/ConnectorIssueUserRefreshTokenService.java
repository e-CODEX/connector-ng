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
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.time.Clock;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for issue of user authentication refresh tokens.
 * This service handles the creation of {@link ConnectorRefreshToken} objects.
 */
@Slf4j
@Service
public class ConnectorIssueUserRefreshTokenService implements ConnectorIssueUserRefreshToken {
    private final ConnectorRefreshTokenRepository repository;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;
    private final Clock clock;

    /**
     * Constructs a new instance of {@link ConnectorIssueUserRefreshTokenService}.
     *
     * @param repository                  The repository for managing refresh tokens.
     * @param authenticationTokenProvider The provider for generating access tokens.
     * @param clock                       The clock used for time-related operations.
     */
    public ConnectorIssueUserRefreshTokenService(
        ConnectorRefreshTokenRepository repository,
        ConnectorAuthenticationTokenProvider authenticationTokenProvider,
        Clock clock) {
        this.repository = repository;
        this.authenticationTokenProvider = authenticationTokenProvider;
        this.clock = clock;
    }


    /**
     * Creates a new refresh token for the given user.
     *
     * @param user user to create the refresh token for
     *
     * @return newly created refresh token
     */
    @Override
    public ConnectorRefreshToken execute(@NonNull ConnectorUser user) {
        log.debug("Encoding and issuing new refresh token for user {}", user.uuid());

        var rawRefreshToken = ConnectorRefreshTokenGenerator.generate();
        var hashedRefreshToken = ConnectorRefreshTokenGenerator.hash(rawRefreshToken);
        var refreshToken = ConnectorRefreshToken.builder()
            .token(hashedRefreshToken)
            .revoked(false)
            .user(user)
            .expiresAt(clock.instant()
                .plus(authenticationTokenProvider.getRefreshTokenExpiresIn()))
            .build();

        var saved = repository.save(refreshToken);

        // issue raw refresh token
        return saved.toBuilder().token(rawRefreshToken).build();
    }
}
