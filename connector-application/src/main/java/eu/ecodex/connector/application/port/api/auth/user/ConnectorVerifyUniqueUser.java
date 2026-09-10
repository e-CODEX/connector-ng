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

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Interface for verifying a unique user to avoid duplicates.
 */
public interface ConnectorVerifyUniqueUser {
    /**
     * Executes an operation for the specified user.
     *
     * @param user the {@link ConnectorUser} on which the operation will be performed; must not be
     *             null.
     */
    void execute(@Nonnull ConnectorUser user);

    /**
     * Executes an operation using the given identifier and user.
     *
     * @param identifier the unique identifier associated with the operation; must not be null
     * @param user       the {@link ConnectorUser} on which the operation will be performed; must
     *                   not be null
     */
    void execute(@Nonnull String identifier, @Nonnull ConnectorUser user);
}
