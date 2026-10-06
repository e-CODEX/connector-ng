/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.auth.user;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;


/**
 * Interface for managing the {@link ConnectorUser} password update after login.
 * Provides methods for updating user password after login.
 */
public interface ConnectorUpdateUserPassword {
    /**
     * Updates an existing {@link ConnectorUser} user password in the system with the provided
     * information.
     *
     * @param passwordUpdateData data used to update user password after login
     *
     * @throws ConnectorUserNotFoundException when user identifier not found
     */
    void execute(@Nonnull ConnectorUpdateUserPasswordCommand passwordUpdateData);
}
