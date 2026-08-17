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

import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing and retrieving {@link ConnectorUser} entities.
 *
 * <p>This service provides functionality to retrieve all registered {@link ConnectorUser} instances
 * by integrating with the underlying persistence layer through the
 * {@link ConnectorUserRepository}.
 * It acts as a bridge between the application logic and the data access layer for user data
 * operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorListUserService implements ConnectorListUser {

    ConnectorUserRepository repository;

    @Override
    public List<ConnectorUser> findAll() {
        return repository.findAll();
    }
}
