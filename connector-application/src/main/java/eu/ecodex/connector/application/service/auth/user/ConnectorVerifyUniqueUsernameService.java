/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.user;

import eu.ecodex.connector.application.exception.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUsername;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to verify if a {@code ConnectorUser} entity has a unique username.
 */
@Slf4j
@Service
public class ConnectorVerifyUniqueUsernameService implements ConnectorVerifyUniqueUsername {
    private final ConnectorUserRepository repository;

    public ConnectorVerifyUniqueUsernameService(ConnectorUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(@Nonnull ConnectorUser user) {
        var usernameTakenByAnotherUser = user.uuid() != null
            ? repository.existsByUsernameAndUuidNot(user.username(), user.uuid())
            : repository.existsByUsername(user.username());

        if (usernameTakenByAnotherUser) {
            throw new ConnectorUserAlreadyExistsException(
                "Username '%s' already exists".formatted(user.username())
            );
        }
    }
}
