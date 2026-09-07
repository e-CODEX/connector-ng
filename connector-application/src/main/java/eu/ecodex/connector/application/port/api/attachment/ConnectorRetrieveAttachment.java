/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.attachment;

import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import jakarta.annotation.Nonnull;

/**
 * Interface defining the contract for retrieving a specific attachment associated with a message in
 * the connector system.
 *
 * <p>Usage of this interface allows clients to retrieve attachments for processing or inspection.
 */
public interface ConnectorRetrieveAttachment {
    /**
     * Retrieves a specific attachment associated with a connector message.
     *
     * <p>This method fetches and returns a {@link ConnectorMessageAttachment} object corresponding
     * to the provided unique identifier. It is used for accessing attachment details such as
     * metadata and storage information.
     *
     * @param identifier the unique identifier of the attachment; must not be null
     *
     * @return the {@link ConnectorMessageAttachment} corresponding to the given identifier
     *
     * @throws NullPointerException if {@code identifier} is null
     */
    ConnectorMessageAttachment execute(@Nonnull String identifier);
}
