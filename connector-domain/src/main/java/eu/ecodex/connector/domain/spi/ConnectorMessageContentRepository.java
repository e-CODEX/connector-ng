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

import eu.ecodex.connector.domain.model.message.content.ConnectorMessageContent;

/**
 * Defines the contract for managing and querying message content within the connector system.
 *
 * <p>Implementations of this interface are expected to handle the persistence, retrieval,
 * and manipulation of message content related to the connector's operations.
 */
public interface ConnectorMessageContentRepository {
    /**
     * Persists the provided message content in the underlying data store.
     *
     * @param messageContent the {@link ConnectorMessageContent} instance to be saved;
     *
     * @return the persisted {@link ConnectorMessageContent} instance, potentially with additional
     *         metadata or state changes applied during the save operation.
     */
    ConnectorMessageContent save(ConnectorMessageContent messageContent);
}
