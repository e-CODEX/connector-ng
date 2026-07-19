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
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for listing message attachments from a connector.
 *
 * <p>This interface is responsible for retrieving a paginated list of
 * {@link ConnectorMessageAttachment} objects associated with a specific message or resource within
 * a connector system.
 *
 * <p>Implementations should handle pagination, filtering, and connector-specific
 * communication details required to fetch attachments.</p>
 */
public interface ConnectorListAttachments {
    /**
     * Executes the attachment listing operation.
     *
     * <p>This method retrieves a paginated result containing
     * {@link ConnectorMessageAttachment} objects based on the provided
     * {@link ConnectorPageRequest}.
     *
     * @param pageRequest the pagination and query parameters used to retrieve the attachments (must
     *                    not be null)
     *
     * @return a {@link ConnectorPageResult} containing a page of {@link ConnectorMessageAttachment}
     *     objects
     *
     * @throws IllegalArgumentException if {@code pageRequest} is invalid
     */
    ConnectorPageResult<ConnectorMessageAttachment> execute(
        @Nonnull ConnectorPageRequest pageRequest);
}
