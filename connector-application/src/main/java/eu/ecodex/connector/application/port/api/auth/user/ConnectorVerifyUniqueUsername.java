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

import eu.ecodex.connector.application.exception.user.ConnectorUserAlreadyExistsException;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import jakarta.annotation.Nonnull;

/**
 * Use case for verifying that a user's username is unique in the connector system, to avoid
 * duplicates.
 */
public interface ConnectorVerifyUniqueUsername {
    /**
     * Verifies that no existing user has the same username as the given user.
     *
     * <p>The method returns normally if the username is unique. If another user already uses it,
     * the failure is reported by an exception.
     *
     * @param user the {@link ConnectorUser} whose username is checked for uniqueness (must not be
     *             null)
     *
     * @throws ConnectorUserAlreadyExistsException if another user already has the same username
     */
    void execute(@Nonnull ConnectorUser user);
}
