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
 * Use case for verifying that a user is unique in the connector system, to avoid duplicates.
 */
public interface ConnectorVerifyUniqueUser {
    /**
     * Verifies that no existing user conflicts with the given user, for example when registering a
     * new user.
     *
     * <p>The method returns normally if the user is unique. If a conflicting user exists, the
     * failure is reported by an exception.
     *
     * @param user the {@link ConnectorUser} to check for uniqueness (must not be null)
     */
    void execute(@Nonnull ConnectorUser user);

    /**
     * Verifies that no existing user, other than the one identified by the given identifier,
     * conflicts with the given user, for example when updating an existing user.
     *
     * <p>The method returns normally if the user is unique. If a conflicting user exists, the
     * failure is reported by an exception.
     *
     * @param identifier the unique identifier of the user being updated, which is excluded from the
     *                   check (must not be null)
     * @param user       the {@link ConnectorUser} carrying the details to check for uniqueness
     *                   (must not be null)
     */
    void execute(@Nonnull String identifier, @Nonnull ConnectorUser user);
}
