/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository interface for managing {@link ConnectorMessageTransportStep} instances within the
 * e-CODEX Connector domain.
 *
 * <p>This interface provides the contract for persisting and manipulating transport steps during
 * the message delivery process. Implementations of this interface are responsible for storing and
 * retrieving {@link ConnectorMessageTransportStep} entities in the underlying persistence
 * mechanism.
 *
 * <p>This interface should be implemented by infrastructure modules to abstract persistence
 * details from the domain layer.
 */
public interface ConnectorMessageTransportStepRepository {
    /**
     * Persists the given {@link ConnectorMessageTransportStep} entity and returns the saved
     * instance.
     *
     * <p>This method is responsible for storing or updating the transport step in the underlying
     * persistence mechanism. Any modifications made to the provided instance are reflected in the
     * returned object.
     *
     * @param transportStep the {@link ConnectorMessageTransportStep} instance to be persisted or
     *                      updated
     *
     * @return the persisted {@link ConnectorMessageTransportStep} instance, updated with any
     *         additional state assigned by the persistence mechanism (e.g., timestamps)
     */
    ConnectorMessageTransportStep save(@Nonnull ConnectorMessageTransportStep transportStep);

    /**
     * Updates an existing {@link ConnectorMessageTransportStep} identified by its unique identifier
     * with the provided updated transport step details.
     *
     * @param identifier    the unique identifier of the {@link ConnectorMessageTransportStep} to be
     *                      updated
     * @param transportStep the updated {@link ConnectorMessageTransportStep} containing the new
     *                      state to be persisted
     *
     * @return the updated {@link ConnectorMessageTransportStep} instance reflecting the saved
     *         changes
     */
    ConnectorMessageTransportStep update(
            @Nonnull String identifier,
            @Nonnull ConnectorMessageTransportStep transportStep);

    /**
     * Updates the status of multiple transport steps identified by their unique identifiers.
     *
     * @param identifiers a list of unique identifiers corresponding to the transport steps whose
     *                    status needs to be updated. The list must not be null and should contain
     *                    valid, existing identifiers.
     * @param status      the new status to be assigned to the identified transport steps. Must not
     *                    be null and must represent a valid instance of
     *                    {@link ConnectorMessageTransportStatus}.
     */
    void updateStatus(
            @Nonnull List<String> identifiers,
            @Nonnull ConnectorMessageTransportStatus status);

    /**
     * Retrieves a {@link ConnectorMessageTransportStep} instance associated with the given message
     * identifier.
     *
     * @param messageIdentifier the unique identifier of the message for which the transport step is
     *                          being retrieved
     *
     * @return the {@link ConnectorMessageTransportStep} associated with the specified message
     *         identifier, or {@code null} if no matching transport step is found
     */
    ConnectorMessageTransportStep findByMessageIdentifier(@Nonnull String messageIdentifier);

    /**
     * Retrieves a {@link ConnectorMessageTransportStep} instance associated with the given unique
     * identifier.
     *
     * @param identifier the unique identifier of the {@link ConnectorMessageTransportStep} to be
     *                   retrieved; must not be null.
     *
     * @return the {@link ConnectorMessageTransportStep} instance identified by the specified
     *         identifier, or {@code null} if no matching transport step is found.
     */
    ConnectorMessageTransportStep findByIdentifier(@Nonnull String identifier);

    /**
     * Retrieves a list of pending transport step identifiers associated with the specified backend
     * system.
     *
     * <p>This method queries the underlying persistence mechanism to find all transport steps that
     * are in a pending state for the provided backend name. Pending transport steps are those that
     * have not yet been submitted to the backend system for processing.
     *
     * @param backendName the name of the backend system for which the pending transport steps are
     *                    to be retrieved; must not be null or empty.
     *
     * @return a list of identifiers for the pending transport steps associated with the specified
     *         backend. If no pending transport steps are found, an empty list is returned.
     */
    List<String> findPendingTransportSteps(@Nonnull String backendName);

    /**
     * Retrieves a list of pending message identifiers associated with the specified backend
     * system.
     *
     * <p>This method queries the underlying persistence mechanism to find all messages that are in
     * a pending state for the provided backend name. Pending messages are those that have not yet
     * been submitted to the backend system for processing.
     *
     * @param backendName the name of the backend system for which the pending messages are to be
     *                    retrieved; must not be null or empty.
     *
     * @return a list of identifiers for the pending messages associated with the specified backend.
     *         If no pending messages are found, an empty list is returned.
     */
    List<String> findPendingMessagesIds(@Nonnull String backendName);
}
