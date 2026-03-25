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

import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;

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

    /**
     * Retrieves a {@link ConnectorMessageAttachment} based on its unique identifier.
     *
     * @param identifier the unique identifier of the attachment to be retrieved; must not be null
     *                   or blank.
     *
     * @return the {@link ConnectorMessageAttachment} matching the specified identifier, or null if
     *         no such attachment exists.
     */
    ConnectorMessageAttachment findByIdentifier(@Nonnull String identifier);

    /**
     * Retrieves a paginated collection of {@link ConnectorMessageAttachment} entities based on the
     * specified {@link ConnectorPageRequest}.
     *
     * @param request the pagination request containing the zero-based page index and the number of
     *                elements per page; must not be null and must adhere to validation rules
     *                defined in {@code ConnectorPageRequest}.
     *
     * @return a {@link ConnectorPageResult} containing a list of {@link ConnectorMessageAttachment}
     *         entities for the requested page, along with pagination metadata such as total
     *         elements and page size.
     */
    ConnectorPageResult<ConnectorMessageAttachment> findAll(ConnectorPageRequest request);

    /**
     * Associates an attachment with a specific message.
     *
     * <p>This method links an attachment identified by its unique identifier
     * to a message identified by its unique identifier. The association typically enables the
     * attached document or file to be retrieved or referenced in communication workflows or logging
     * purposes related to the message.
     *
     * @param attachmentIdentifier a non-null and non-blank string representing the unique
     *                             identifier of the attachment to be linked.
     * @param messageIdentifier    a non-null and non-blank string representing the unique
     *                             identifier of the message to which the attachment has to be
     *                             associated.
     */
    void attachToMessage(@Nonnull String attachmentIdentifier, @Nonnull String messageIdentifier);

    void updateType(@Nonnull String attachmentIdentifier, @Nonnull ConnectorAttachmentType type);
}
