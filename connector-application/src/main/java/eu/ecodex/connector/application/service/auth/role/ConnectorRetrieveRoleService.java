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

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRole;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for retrieving user roles within the Connector system.
 * This class provides methods to fetch user role details based on a unique identifier or a
 * username.
 * It interacts with the {@link ConnectorRoleRepository} to query the underlying data source.
 *
 * <p>The service is designed to throw a {@link NotFoundException} if a requested user role
 * cannot be located by the provided identifier or name.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRetrieveRoleService implements ConnectorRetrieveRole {

    ConnectorRoleRepository repository;

    @Override
    public ConnectorRole getById(String identifier) throws ConnectorRoleNotFoundException {
        return repository
            .findByUuid(identifier)
            .orElseThrow(() -> new ConnectorRoleNotFoundException(
                String.format("User not found by identifier %s", identifier)));
    }

    @Override
    public ConnectorRole getByName(String name) throws ConnectorRoleNotFoundException {
        return repository
            .findByName(name)
            .orElseThrow(() -> new ConnectorRoleNotFoundException(
                String.format("User not found by username %s", name)));
    }

    @Override
    public Set<ConnectorRole> findByNameIn(Set<String> roles) {
        return repository.findByNameIn(roles);
    }

}
