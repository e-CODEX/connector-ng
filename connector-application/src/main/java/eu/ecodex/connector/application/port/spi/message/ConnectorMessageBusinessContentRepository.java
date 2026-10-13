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

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import jakarta.annotation.Nonnull;

/**
 * Repository (port) for persisting the business content of connector messages.
 *
 * <p>Implementations handle the persistence and update of the business content related to the
 * connector's operations.
 */
public interface ConnectorMessageBusinessContentRepository {
    /**
     * Persists the given business content for the given message.
     *
     * @param businessContent   the {@link ConnectorMessageBusinessContent} to save (must not be
     *                          null)
     * @param messageIdentifier the identifier of the {@link ConnectorBusinessMessage} the content
     *                          belongs to (must not be null)
     *
     * @return the persisted {@link ConnectorMessageBusinessContent}, potentially with additional
     *     metadata or state changes applied during the save operation
     */
    ConnectorMessageBusinessContent save(
        @Nonnull ConnectorMessageBusinessContent businessContent,
        @Nonnull String messageIdentifier);

    /**
     * Assigns a printable business document to the message business content identified by the given
     * UUID.
     *
     * @param uuid     the unique identifier of the message business content to which the document
     *                 is assigned (must not be null)
     * @param document the {@link ConnectorMessageBusinessDocument} to assign (must not be null)
     *
     * @return the updated {@link ConnectorMessageBusinessContent} with the assigned business
     *     document
     */
    ConnectorMessageBusinessContent assignBusinessDocument(
        @Nonnull String uuid, @Nonnull ConnectorMessageBusinessDocument document);
}
