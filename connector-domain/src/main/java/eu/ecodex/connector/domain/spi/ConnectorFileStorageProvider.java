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

import jakarta.annotation.Nonnull;
import java.io.InputStream;

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
     * Stores a file in the underlying storage system.
     *
     * @param filename    the original name of the file; may be used to determine storage path or
     *                    naming conventions
     * @param fileSize    the size of the file in bytes; may be {@code null} if unknown
     * @param contentType the MIME type of the file (e.g., {@code "application/pdf"},
     *                    {@code "image/png"}); may be {@code null} if not provided
     * @param inputStream the input stream containing the file data; must not be {@code null}
     *
     * @return a unique identifier, URL, or storage key representing the saved file; never
     *         {@code null}
     * @throws IllegalArgumentException if required parameters are invalid
     * @throws RuntimeException         if the file cannot be stored due to I/O errors or storage
     *                                  provider failures
     */
    String save(
            String filename,
            Long fileSize,
            String contentType,
            @Nonnull InputStream inputStream
    );
}
