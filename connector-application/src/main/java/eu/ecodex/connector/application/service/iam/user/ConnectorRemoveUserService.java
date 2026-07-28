/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.user;

import eu.ecodex.connector.application.port.api.iam.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.spi.iam.user.ConnectorUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing the removal of ConnectorUser entities.
 * <p>
 * This class provides functionality to delete a user from the system by their unique identifier.
 * It interacts with the underlying persistence layer to ensure that the specified user exists
 * before performing the removal operation. If the user is not found, an exception is thrown.
 * <p>
 * An instance of {@link ConnectorUserRepository} is used to perform database operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRemoveUserService implements ConnectorRemoveUser {

    ConnectorUserRepository repository;

    @Override
    public void deleteById(String identifier) {
        repository.deleteByUuid(identifier);
    }
}
