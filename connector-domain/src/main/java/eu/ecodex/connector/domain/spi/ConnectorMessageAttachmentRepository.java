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


import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;

/**
 * Repository interface for managing {@link ConnectorMessageAttachment}.
 *
 * <p>This interface defines the contract for storing and retrieving attachments that are
 * associated with messages. Attachments typically include documents, such as PDFs, that need to be
 * sent as part of a message. Implementations of this repository interface handle storage operations
 * for these attachments.
 */
public interface ConnectorMessageAttachmentRepository {
    /**
     * Persists a given ConnectorMessageAttachment entity into the underlying data store. The
     * attachment typically includes metadata and references to documents or files that are intended
     * to be sent along with a message. This method ensures that the attachment is stored and made
     * retrievable for future use.
     *
     * @param attachment the ConnectorMessageAttachment entity to be saved, containing details such
     *                   as the attachment's identifier, name, MIME type, and description.
     *
     * @return the saved ConnectorMessageAttachment entity, including any updates applied during the
     *         persistence process, such as the generation of an identifier.
     */
    ConnectorMessageAttachment save(ConnectorMessageAttachment attachment);

    ConnectorPageResult<ConnectorMessageAttachment> findAll(ConnectorPageRequest request);
}
