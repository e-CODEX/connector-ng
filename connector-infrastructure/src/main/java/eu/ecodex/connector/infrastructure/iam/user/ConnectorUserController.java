/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.iam.user;

import eu.ecodex.connector.application.exception.ConnectorUserBadRequestException;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorListUser;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRegisterUser;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRemoveUser;
import eu.ecodex.connector.application.port.api.iam.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing connector's users.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserController implements ConnectorUserApi {
    ConnectorRegisterUser connectorRegisterUser;
    ConnectorRetrieveUser connectorRetrieveUser;
    ConnectorRemoveUser connectorRemoveUser;
    ConnectorListUser connectorListUser;
    PasswordEncoder passwordEncoder;


    @Override
    public ConnectorUserDto register(ConnectorUserDto userDto) {
        log.info("Registering new user");
        userDto = encodePassword(userDto);
        var registered = connectorRegisterUser.register(ConnectorUserDto.toDomain(userDto));
        log.info("New user registered");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto update(Long id, ConnectorUserDto userDto) {
        log.info("Updating existing user");
        userDto = encodePassword(userDto);
        var updated = connectorRegisterUser.update(id, ConnectorUserDto.toDomain(userDto));
        log.info("Existing user updated");
        return ConnectorUserDto.from(updated);
    }

    @Override
    public ConnectorUserDto patch(Long id, ConnectorUserDto userDto) {
        log.info("Patching existing user");
        if (userDto.password() != null) {
            userDto = encodePassword(userDto);
        }
        var registered = connectorRegisterUser.patch(id, ConnectorUserDto.toDomain(userDto));
        log.info("Existing user patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getById(Long identifier) {
        ConnectorUser userById = connectorRetrieveUser.getById(identifier);
        return ConnectorUserDto.from(userById);
    }

    @Override
    public List<ConnectorUserDto> getAll() {
        return connectorListUser.findAll().stream().map(ConnectorUserDto::from).toList();
    }

    @Override
    public void delete(ConnectorUserDto userDto) {
        connectorRemoveUser.delete(ConnectorUserDto.toDomain(userDto));
        log.info("User deleted");
    }

    @Override
    public void deleteById(Long userIdentifier) {
        connectorRemoveUser.deleteById(userIdentifier);
        log.info("User deleted by id");
    }

    private ConnectorUserDto encodePassword(ConnectorUserDto userDto) {
        var encodedPassword = passwordEncoder.encode(userDto.password());
        if (encodedPassword == null) {
            throw new ConnectorUserBadRequestException("Error occurs during password encoding");
        }
        return userDto.toBuilder()
                .password(encodedPassword)
                .build();

    }
}
