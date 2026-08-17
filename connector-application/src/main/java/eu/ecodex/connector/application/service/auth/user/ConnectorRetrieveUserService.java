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

import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRetrieveUser} interface, providing services for retrieving
 * {@link ConnectorUser} entities from a repository based on various attributes such as identifier,
 * username, email, or a combination of username and email.
 *
 * <p>This service integrates with a {@link ConnectorUserRepository} to perform data operations and
 * throws a {@link NotFoundException} when a user cannot be found based on the provided parameters.
 *
 * <p>Thread safety: This class is designed as a stateless Spring {@code @Service}, and its methods
 * are thread-safe as long as the underlying {@link ConnectorUserRepository} is thread-safe.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRetrieveUserService implements ConnectorRetrieveUser {

    ConnectorUserRepository repository;

    @Override
    public ConnectorUser getById(String identifier) throws NotFoundException {
        return repository
                .findByUuid(identifier)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User not found by identifier %s", identifier)));
    }

    @Override
    public ConnectorUser getByUsername(String username) throws NotFoundException {
        return repository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User not found by username %s", username)));
    }

    @Override
    public ConnectorUser getByEmail(String email) throws NotFoundException {
        return repository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User not found by email %s", email)));
    }

    @Override
    public ConnectorUser getByUsernameAndEmail(String username, String email)
            throws NotFoundException {
        return repository
                .findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User not found by username %s and email %s", username,
                                email)));
    }

    @Override
    public Optional<ConnectorUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
