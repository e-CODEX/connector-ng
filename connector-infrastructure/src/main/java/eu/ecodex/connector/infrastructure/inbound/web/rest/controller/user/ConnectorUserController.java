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

import eu.ecodex.connector.application.port.api.auth.user.ConnectorEditUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.user.ConnectorUserDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.ConnectorUpdateUserPasswordRequest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.user.ConnectorEditSelfRequest;
import eu.ecodex.connector.infrastructure.outbound.auth.identity.ConnectorUserDetails;
import lombok.NonNull;
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
    private final ConnectorEditUser editUser;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorUpdateUserPassword updateUserPassword;

    /**
     * Constructs a {@code ConnectorUserController} with the necessary services.
     *
     * @param editUser                 service to patch user
     * @param retrieveUserByIdentifier service to retrieve user
     */
    public ConnectorUserController(ConnectorEditUser editUser,
                                   ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier,
                                   ConnectorUpdateUserPassword updateUserPassword) {
        this.editUser = editUser;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
        this.updateUserPassword = updateUserPassword;
    }

    @Override
    public ConnectorUserDto patch(@NonNull ConnectorUserDetails userDetails,
                                  @NonNull ConnectorEditSelfRequest userRequest) {
        log.info("Editing existing user");
        var registered = editUser.execute(userDetails.getUserId(),
            ConnectorEditSelfRequest.toCommand(userDetails.getUserId(), userRequest));

        log.info("User patched");
        return ConnectorUserDto.from(registered);
    }

    @Override
    public ConnectorUserDto getByIdentifier(@NonNull ConnectorUserDetails userDetails) {
        log.info("Retrieving user {} details", userDetails.getUserId());
        var found = retrieveUserByIdentifier.execute(userDetails.getUserId());
        log.info("User {} found", userDetails.getUserId());
        return ConnectorUserDto.from(found);
    }

    @Override
    public void updatePassword(@NonNull ConnectorUserDetails userDetails,
                               @NonNull ConnectorUpdateUserPasswordRequest userPasswordRequest) {
        log.info("Updating user {} password", userDetails.getUserId());
        var passwordUpdateData = ConnectorUpdateUserPasswordRequest.from(
            userDetails.getUserId(), userDetails.accessToken(), userPasswordRequest);

        updateUserPassword.execute(passwordUpdateData);
    }
}
