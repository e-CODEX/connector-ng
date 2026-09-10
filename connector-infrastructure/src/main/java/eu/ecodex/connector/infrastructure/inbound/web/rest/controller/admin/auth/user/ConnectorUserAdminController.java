/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.auth.user;

import static eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest.toDomain;

import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUser;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing connector users. Provides APIs for operations such as registration,
 * updating, partial updates, retrieval, listing, and deletion of users.
 *
 * <p>This controller relies on service classes for handling user-related operations and
 * ensures additional processing like password encoding before delegation.
 */
@Slf4j
@RestController
public class ConnectorUserAdminController implements ConnectorUserAdminApi {
    private final ConnectorRetrieveUserByIdentifier connectorRetrieveUserByIdentifier;
    private final ConnectorRegisterUser connectorRegisterUser;
    private final ConnectorUpdateUser connectorUpdateUser;
    private final ConnectorPatchUser connectorPatchUser;
    private final ConnectorRemoveUser connectorRemoveUser;
    private final ConnectorListUser connectorListUser;

    /**
     * Constructs an instance of {@code ConnectorUserAdminController} with the required
     * service interfaces for managing {@link ConnectorUser} entities.
     *
     * @param connectorRetrieveUserByIdentifier service for retrieving {@link ConnectorUser}
     *                                          entities by identifier
     * @param connectorRegisterUser             service for registering new {@link ConnectorUser}
     *                                          entities in the system
     * @param connectorUpdateUser               service for updating existing {@link ConnectorUser}
     *                                          entities in the system
     * @param connectorPatchUser                service for partially updating {@link ConnectorUser}
     *                                          entities by applying specified changes
     * @param connectorRemoveUser               service for deleting {@link ConnectorUser} entities
     *                                          from the system
     * @param connectorListUser                 service for retrieving a list of all
     *                                          {@link ConnectorUser}
     *                                          entities along with their roles
     */
    public ConnectorUserAdminController(
        ConnectorRetrieveUserByIdentifier connectorRetrieveUserByIdentifier,
        ConnectorRegisterUser connectorRegisterUser, ConnectorUpdateUser connectorUpdateUser,
        ConnectorPatchUser connectorPatchUser, ConnectorRemoveUser connectorRemoveUser,
        ConnectorListUser connectorListUser) {
        this.connectorRetrieveUserByIdentifier = connectorRetrieveUserByIdentifier;
        this.connectorRegisterUser = connectorRegisterUser;
        this.connectorUpdateUser = connectorUpdateUser;
        this.connectorPatchUser = connectorPatchUser;
        this.connectorRemoveUser = connectorRemoveUser;
        this.connectorListUser = connectorListUser;
    }


    @Override
    public ConnectorUserDto register(ConnectorUserRequest userRequest) {
        log.info("Registering new user");
        var registered = connectorRegisterUser.execute(toDomain(userRequest));

        log.info("New user registered");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto update(@NonNull String identifier,
                                   @Valid ConnectorUserRequest userRequest) {
        log.info("Updating existing user");
        var updated = connectorUpdateUser.execute(identifier, toDomain(userRequest));

        log.info("User updated");
        return ConnectorUserDto.from(updated);
    }

    @Override
    public ConnectorUserDto patch(@NonNull String identifier,
                                  @Valid ConnectorUserRequest userRequest) {
        log.info("Patching existing user");
        var registered = connectorPatchUser.execute(identifier, toDomain(userRequest));

        log.info("User patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getByIdentifier(@NonNull String identifier) {
        ConnectorUser userById = connectorRetrieveUserByIdentifier.execute(identifier);
        return ConnectorUserDto.from(userById);
    }

    @Override
    public List<ConnectorUserDto> getAll() {
        return connectorListUser.execute().stream().map(ConnectorUserDto::from).toList();
    }

    @Override
    public void deleteByIdentifier(@NonNull String userIdentifier) {
        connectorRemoveUser.execute(userIdentifier);
        log.info("User deleted by identifier");
    }
}
