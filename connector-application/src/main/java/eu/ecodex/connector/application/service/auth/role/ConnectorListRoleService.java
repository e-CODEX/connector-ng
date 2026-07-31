/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.role;

import eu.ecodex.connector.application.port.api.auth.role.ConnectorListRole;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for retrieving all user roles associated with the Connector system.
 * This class interacts with the {@link ConnectorRoleRepository} to query the data source
 * and fetch a list of all available {@link ConnectorRole} instances.
 *
 * <p>
 * Responsibilities:
 * - Provides a mechanism for retrieving all user roles from the underlying data source.
 * - Acts as a bridge between the application's business logic and the persistence layer for user roles.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorListRoleService implements ConnectorListRole {

    ConnectorRoleRepository repository;

    @Override
    public List<ConnectorRole> findAll() {
        return repository.findAll();
    }
}
