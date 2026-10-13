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

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Describes a file received for upload, stored temporarily on disk until it is processed.
 *
 * <p>The content of the file is not held in memory: it is referenced by
 * {@code tempFileLocation}. Once the file has been processed, the caller should call
 * {@link #cleanup()} to delete the temporary file.
 *
 * @param filename         original name of the uploaded file (must not be null)
 * @param size             size of the file in bytes
 * @param contentType      MIME type of the file, for example {@code application/pdf} (must not be
 *                         null)
 * @param tempFileLocation location of the temporary file holding the uploaded content (must not be
 *                         null)
 * @param description      free-text description of the file (must not be null)
 */
@Slf4j
@Builder
public record FileUploadCommand(
    @Nonnull String filename,
    long size,
    @Nonnull String contentType,
    @Nonnull Path tempFileLocation,
    @Nonnull String description
) {
    /**
     * Deletes the temporary file referenced by {@link #tempFileLocation()}.
     *
     * <p>The deletion is the best effort: a missing file is ignored, and an {@link IOException}
     * during deletion is logged as an error and not propagated to the caller.
     */
    public void cleanup() {
        try {
            Files.deleteIfExists(tempFileLocation);
        } catch (IOException e) {
            log.error("Failed to delete temporary file {}", tempFileLocation, e);
        }
    }
}
