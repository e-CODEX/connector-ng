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
import java.util.Optional;

/**
 * Service interface for retrieving {@link ConnectorUser} entities by different attributes.
 * Provides methods to retrieve users by their unique identifier, username, email, or a combination
 * of username and email.
 */
public interface ConnectorRetrieveUser {
    /**
     * Retrieves a {@link ConnectorUser} instance by its unique identifier.
     *
     * @param identifier the unique identifier of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the given identifier
     *
     * @throws ConnectorUserNotFoundException if no user is found with the specified identifier
     */
    ConnectorUser getByIdentifier(String identifier) throws ConnectorUserNotFoundException;

    /**
     * Retrieves a {@link ConnectorUser} instance by its unique username.
     *
     * @param username the unique username of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the specified username
     *
     * @throws ConnectorUserNotFoundException if no user is found with the given username
     */
    ConnectorUser getByUsername(String username) throws ConnectorUserNotFoundException;

    /**
     * Retrieves a {@link ConnectorUser} instance based on the provided email address.
     *
     * @param email the email address of the user to retrieve
     *
     * @return the {@link ConnectorUser} associated with the specified email address
     *
     * @throws ConnectorUserNotFoundException if no user is found with the given email address
     */
    ConnectorUser getByEmail(String email) throws ConnectorUserNotFoundException;

    /**
     * Retrieves a {@link ConnectorUser} instance based on the provided username and email address.
     *
     * @param username the unique username associated with the user
     * @param email    the email address associated with the user
     *
     * @return the {@link ConnectorUser} associated with the specified username and email address
     *
     * @throws ConnectorUserNotFoundException if no user is found with the given username and email
     *                                        address
     */
    ConnectorUser getByUsernameAndEmail(String username, String email) throws
        ConnectorUserNotFoundException;

    /**
     * Retrieves an {@link Optional} containing a {@link ConnectorUser} instance associated with the
     * given username.
     * If no user is found with the specified username, an empty {@link Optional} is returned.
     *
     * @param username the unique username of the user to retrieve
     *
     * @return an {@link Optional} containing the {@link ConnectorUser} if found, or an empty
     *     {@link Optional} if not
     */
    Optional<ConnectorUser> findByUsername(String username);
}
