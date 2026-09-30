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

import eu.ecodex.connector.application.exception.ConnectorUserBadCredentialsException;
import eu.ecodex.connector.application.exception.ConnectorUserInvalidPasswordException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorPatchUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.domain.model.auth.ConnectorUpdateUserPasswordData;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Connector user password update.
 */
@Slf4j
@Service
public class ConnectorUpdateUserPasswordService implements ConnectorUpdateUserPassword {
    private final ConnectorPatchUser patchUser;
    private final ConnectorUserPasswordEncoder passwordEncoder;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    /**
     * Constructs an instance of {@code ConnectorUpdateUserPassword}.
     */
    public ConnectorUpdateUserPasswordService(
        ConnectorPatchUser patchUser, ConnectorUserPasswordEncoder passwordEncoder,
        ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier,
        ConnectorAuthenticationTokenProvider authenticationTokenProvider) {
        this.patchUser = patchUser;
        this.passwordEncoder = passwordEncoder;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
        this.authenticationTokenProvider = authenticationTokenProvider;
    }

    @Override
    public void execute(@NonNull ConnectorUpdateUserPasswordData passwordUpdateData) {
        if (passwordUpdateData.currentPassword().equals(passwordUpdateData.newPassword())) {
            throw new ConnectorUserInvalidPasswordException(
                "The new password must be different from the current password.");
        }
        var connectorUser = retrieveUserByIdentifier.execute(passwordUpdateData.uuid());
        var usernameFromToken = authenticationTokenProvider.getUsernameFromToken(
            passwordUpdateData.accessToken());
        if (!usernameFromToken.equals(connectorUser.username())) {
            throw new ConnectorUserBadCredentialsException(
                "Access token does not match the target user.");
        }
        boolean mustChangePassword =
            connectorUser.mustChangePassword() == null || connectorUser.mustChangePassword();

        if (!mustChangePassword) {
            log.warn("No need to change password for user '{}' — mustChangePassword is false.",
                connectorUser.username());
            return;
        }
        if (!passwordEncoder.matches(passwordUpdateData.currentPassword(),
            connectorUser.password())) {
            throw new ConnectorUserInvalidPasswordException(
                "The current password provided is incorrect.");
        }

        var updatePasswordData = ConnectorUser.builder()
            .uuid(connectorUser.uuid())
            .username(connectorUser.username())
            .password(passwordUpdateData.newPassword())
            .build();

        var patched = patchUser.execute(connectorUser.uuid(), updatePasswordData);
        log.info("User '{}' password is successfully updated.", patched.username());
    }
}
