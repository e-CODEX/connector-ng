/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.user;

import static eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest.toDomain;

import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorUserRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.login.ConnectorUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's profile within the connector system.
 * Provides endpoints for partially updating user information and retrieving user details.
 *
 * <p>This controller implements the {@link ConnectorUserApi} interface, which defines the contract
 * for user management operations such as patching existing user data and retrieving user
 * information based on authentication details.</p>
 *
 */
@Slf4j
@RestController
public class ConnectorUserController implements ConnectorUserApi {
    private final ConnectorPatchUser patchUser;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;

    public ConnectorUserController(ConnectorPatchUser patchUser,
                                   ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier) {
        this.patchUser = patchUser;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
    }

    @Override
    public ConnectorUserDto patch(ConnectorUserDetails userDetails,
                                  ConnectorUserRequest userRequest) {
        log.info("Patching existing user");
        var registered =
            patchUser.execute(userDetails.getUserId(), toDomain(userRequest));
        log.info("User patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getByIdentifier(ConnectorUserDetails userDetails) {
        var found = retrieveUserByIdentifier.execute(userDetails.getUserId());
        return ConnectorUserDto.from(found);
    }
}
