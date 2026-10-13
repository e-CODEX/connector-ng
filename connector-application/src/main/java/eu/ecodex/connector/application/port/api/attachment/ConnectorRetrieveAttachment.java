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
 * Use case for retrieving the metadata of a connector message attachment.
 */
public interface ConnectorRetrieveAttachment {
    /**
     * Retrieves the {@link ConnectorMessageAttachment} identified by the given identifier.
     *
     * @param identifier unique identifier of the attachment to retrieve (must not be null)
     *
     * @return the {@link ConnectorMessageAttachment} associated with the given identifier
     */
    ConnectorMessageAttachment execute(@Nonnull String identifier);
}
