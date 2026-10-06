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

import eu.ecodex.connector.application.exception.ConnectorUserPasswordUpdateNotRequiredException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUserByIdentifier;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPassword;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordAtFirstLogin;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorUpdateUserPasswordCommand;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Connector user password update at first login.
 */
@Slf4j
@Service
public class ConnectorUpdateUserPasswordAtFirstLoginService implements
    ConnectorUpdateUserPasswordAtFirstLogin {
    private final ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier;
    private final ConnectorUpdateUserPassword updateUserPassword;

    /**
     * Constructs an instance of {@code ConnectorUpdateUserPasswordAtFirstLogin}.
     */
    public ConnectorUpdateUserPasswordAtFirstLoginService(
        ConnectorRetrieveUserByIdentifier retrieveUserByIdentifier,
        ConnectorUpdateUserPassword updateUserPassword) {
        this.updateUserPassword = updateUserPassword;
        this.retrieveUserByIdentifier = retrieveUserByIdentifier;
    }

    @Override
    public void execute(@NonNull ConnectorUpdateUserPasswordCommand passwordUpdateData) {
        var user = retrieveUserByIdentifier.execute(passwordUpdateData.uuid());
        if (!user.mustChangePassword()) {
            log.warn("Password update rejected for user {}: no password change is pending",
                passwordUpdateData.uuid());
            throw new ConnectorUserPasswordUpdateNotRequiredException(
                "No mandatory password change is pending");
        }

        updateUserPassword.execute(passwordUpdateData);
    }
}
