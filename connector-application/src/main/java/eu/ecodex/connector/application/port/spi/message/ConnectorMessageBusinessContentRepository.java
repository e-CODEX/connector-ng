/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for managing and querying message businessContent within the connector
 * system.
 *
 * <p>Implementations of this interface are expected to handle the persistence, retrieval,
 * and manipulation of message businessContent related to the connector's operations.
 */
public interface ConnectorMessageBusinessContentRepository {
    /**
     * Persists the provided message businessContent in the underlying data store.
     *
     * @param businessContent   the {@link ConnectorMessageBusinessContent} instance to be saved;
     * @param messageIdentifier the {@link ConnectorMessage} identifier;
     *
     * @return the persisted {@link ConnectorMessageBusinessContent} instance, potentially with
     *     additional metadata or state changes applied during the save operation.
     */
    ConnectorMessageBusinessContent save(
        @Nonnull ConnectorMessageBusinessContent businessContent,
        @Nonnull String messageIdentifier);

    /**
     * Assigns a printable business document to a message's business content identified by the given
     * UUID.
     *
     * @param uuid     the unique identifier of the message business content to which the business
     *                 document will be assigned; must not be null
     * @param document the business document to be assigned to the message business content; must
     *                 not be null
     *
     * @return the updated {@link ConnectorMessageBusinessContent} instance with the assigned
     *     business document
     */
    ConnectorMessageBusinessContent assignBusinessDocument(
        @Nonnull String uuid, @Nonnull ConnectorMessageBusinessDocument document);
}
