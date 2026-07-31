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

import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing connector users. Provides APIs for operations such as registration,
 * updating, partial updates, retrieval, listing, and deletion of users.
 *
 * <p>
 * This controller relies on service classes for handling user-related operations and
 * ensures additional processing like password encoding before delegation.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserAdminController implements ConnectorUserAdminApi {
    ConnectorRegisterUser connectorRegisterUser;
    ConnectorRetrieveUser connectorRetrieveUser;
    ConnectorRemoveUser connectorRemoveUser;
    ConnectorListUser connectorListUser;
    PasswordEncoder passwordEncoder;


    @Override
    public ConnectorUserDto register(ConnectorUserRequest userRequest) {
        log.info("Registering new user");
        userRequest = encodePassword(userRequest);

        var registered = connectorRegisterUser.register(toDomain(userRequest));

        log.info("New user registered");
        return ConnectorUserDto.from(registered);
    }


    @Override
    public ConnectorUserDto update(String identifier, ConnectorUserRequest userRequest) {
        log.info("Updating existing user");
        userRequest = encodePassword(userRequest);

        var updated = connectorRegisterUser.update(identifier, toDomain(userRequest));

        log.info("User updated");
        return ConnectorUserDto.from(updated);
    }

    @Override
    public ConnectorUserDto patch(String id, ConnectorUserRequest userRequest) {
        log.info("Patching existing user");
        if (userRequest.password() != null) {
            userRequest = encodePassword(userRequest);
        }

        var registered = connectorRegisterUser.patch(id, toDomain(userRequest));
        log.info("User patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getById(String identifier) {
        ConnectorUser userById = connectorRetrieveUser.getById(identifier);
        return ConnectorUserDto.from(userById);
    }

    @Override
    public List<ConnectorUserDto> getAll() {
        return connectorListUser.findAll().stream().map(ConnectorUserDto::from).toList();
    }

    @Override
    public void deleteByIdentifier(String userIdentifier) {
        connectorRemoveUser.deleteById(userIdentifier);
        log.info("User deleted by identifier");
    }


    private ConnectorUserRequest encodePassword(ConnectorUserRequest userRequest) {
        var encodedPassword = passwordEncoder.encode(userRequest.password());
        if (encodedPassword == null) {
            throw new ConnectorUserBadRequestException("Error occurs during password encoding");
        }
        return userRequest.toBuilder()
                .password(encodedPassword)
                .build();

    }
}
