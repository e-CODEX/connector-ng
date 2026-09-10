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

import eu.ecodex.connector.application.exception.ConnectorUserIdMismatchException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUser;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUserEmail;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorVerifyUniqueUsername;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service to verify if a {@code ConnectorUser} entity has a unique username and email.
 */
@Slf4j
@Service
public class ConnectorVerifyUniqueUserService implements ConnectorVerifyUniqueUser {
    private final ConnectorVerifyUniqueUserEmail verifierUniqueUserEmail;
    private final ConnectorVerifyUniqueUsername verifyUniqueUsername;

    public ConnectorVerifyUniqueUserService(
        ConnectorVerifyUniqueUserEmail connectorVerifierUniqueUserEmail,
        ConnectorVerifyUniqueUsername connectorVerifyUniqueUsername) {
        this.verifierUniqueUserEmail = connectorVerifierUniqueUserEmail;
        this.verifyUniqueUsername = connectorVerifyUniqueUsername;
    }

    @Override
    public void execute(@NonNull String identifier, @NonNull ConnectorUser user) {
        if (user.uuid() != null && !user.uuid().equals(identifier)) {
            throw new ConnectorUserIdMismatchException(
                "identifier '%s' does not match the user identifier '%s'"
                    .formatted(identifier, user.uuid()));
        }
        var newUser = user.toBuilder().uuid(identifier).build();
        execute(newUser);
    }

    @Override
    public void execute(@NonNull ConnectorUser user) {
        verifierUniqueUserEmail.execute(user);
        verifyUniqueUsername.execute(user);
    }
}
