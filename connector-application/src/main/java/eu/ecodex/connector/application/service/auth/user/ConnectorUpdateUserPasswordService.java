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
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordCommand;
import eu.ecodex.connector.application.port.spi.auth.accesstoken.ConnectorAuthenticationTokenProvider;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserPasswordEncoder;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Connector user password update.
 */
@Slf4j
@Service
public class ConnectorUpdateUserPasswordService implements ConnectorUpdateUserPassword {
    private final ConnectorUserPasswordEncoder passwordEncoder;
    private final ConnectorUserRepository repository;
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorAuthenticationTokenProvider authenticationTokenProvider;

    /**
     * Constructs an instance of {@code ConnectorUpdateUserPassword}.
     */
    public ConnectorUpdateUserPasswordService(
        ConnectorUserPasswordEncoder passwordEncoder,
        ConnectorUserRepository repository,
        ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier,
        ConnectorAuthenticationTokenProvider authenticationTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
        this.authenticationTokenProvider = authenticationTokenProvider;
    }

    @Override
    public void execute(@NonNull ConnectorUpdateUserPasswordCommand passwordUpdateData) {
        log.info("Updating user {} password", passwordUpdateData.uuid());
        if (passwordUpdateData.currentPassword().equals(passwordUpdateData.newPassword())) {
            log.error("Password update rejected for user {}: new password equals current password",
                passwordUpdateData.uuid());

            throw new ConnectorUserInvalidPasswordException(
                "The new password must be different from the current password.");
        }

        var user = retrieveUserByIdentifier.execute(passwordUpdateData.uuid());

        if (passwordUpdateData.accessToken() != null) {
            var usernameFromToken = authenticationTokenProvider.getUsernameFromToken(
                passwordUpdateData.accessToken());
            if (!usernameFromToken.equals(user.username())) {
                log.error(
                    "Password update rejected for user {}: access token does not match the user",
                    passwordUpdateData.uuid());
                throw new ConnectorUserBadCredentialsException(
                    "Access token does not match the target user.");
            }
        }

        if (!passwordEncoder.matches(passwordUpdateData.currentPassword(), user.password())) {
            log.warn("Password update rejected for user {}: current password is incorrect",
                passwordUpdateData.uuid());
            throw new ConnectorUserInvalidPasswordException(
                "The current password provided is incorrect.");
        }

        var encodedPassword = passwordEncoder.encodePassword(passwordUpdateData.newPassword());
        var updatedUser = user.changePassword(encodedPassword);
        repository.save(updatedUser);
    }
}
