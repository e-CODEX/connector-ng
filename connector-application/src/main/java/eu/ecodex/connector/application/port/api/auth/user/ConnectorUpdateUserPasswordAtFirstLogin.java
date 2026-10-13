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

import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;


/**
 * Use case for changing the password of a user at their first login.
 *
 * <p>This is typically used when a user account was created with a temporary or initial password
 * that must be replaced before the account can be used normally.
 */
public interface ConnectorUpdateUserPasswordAtFirstLogin {
    /**
     * Updates the password of an existing {@link ConnectorUser} at their first login, with the data
     * provided in the given command.
     *
     * @param passwordUpdateData the {@link ConnectorUpdateUserPasswordCommand} carrying the data
     *                           used to update the user's password (must not be null)
     *
     * @throws ConnectorUserNotFoundException if no user matches the user identifier carried by the
     *                                        command
     */
    void execute(@Nonnull ConnectorUpdateUserPasswordCommand passwordUpdateData);
}
