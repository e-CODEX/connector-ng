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
import jakarta.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Defines a contract for storing files in an external or internal storage system.
 *
 * <p>Implementations of this interface are responsible for persisting file content
 * to a storage backend such as a local filesystem, cloud storage service, database, or third-party
 * provider.
 *
 * <p>The implementation must handle stream consumption, storage location
 * resolution, and any required metadata handling.
 */
public interface ConnectorFileStorageProvider {
    /**
     * Saves the given attachment content to the underlying storage system.
     *
     * @param attachment a non-null {@link ConnectorMessageAttachment} metadata associated with the
     *                   file being stored
     * @param filePath   the path to the file on the local filesystem to be stored (must not be
     *                   null)
     *
     * @return a {@link String} representing the storage reference (e.g., object key, path, or URL)
     *         of the saved file
     * @throws NullPointerException if {@code inputStream} is null
     */
    String save(@Nonnull ConnectorMessageAttachment attachment, @Nonnull Path filePath);

    String save(@Nonnull ConnectorMessageAttachment attachment, @Nonnull byte[] content);

    byte[] findByIdentifier(String identifier);
}
