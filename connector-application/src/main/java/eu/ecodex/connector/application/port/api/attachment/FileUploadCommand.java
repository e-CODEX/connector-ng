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
 * Command object representing a single file upload request.
 *
 * <p>This record encapsulates both file metadata and its binary content and is typically used as a
 * transport object between application layers.
 *
 * <p>Instances of this record are immutable with respect to their fields.
 *
 * @param filename         the original name of the file; must not be {@code null}
 * @param size             the size of the file in bytes; must be greater than or equal to zero
 * @param contentType      the MIME type of the file (e.g. {@code "application/pdf"},
 *                         {@code "image/png"}); may not be {@code null}
 * @param tempFileLocation the {@link Path} pointing to the temporary file location; may not be
 *                         {@code null}
 * @param description      a description of the file content or purpose; may not be {@code null}
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
     * Deletes the temporary file.
     */
    public void cleanup() {
        try {
            Files.deleteIfExists(tempFileLocation);
        } catch (IOException e) {
            log.error("Failed to delete temporary file {}", tempFileLocation, e);
        }
    }
}
