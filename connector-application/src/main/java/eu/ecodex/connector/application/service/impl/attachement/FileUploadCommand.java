/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.attachement;

import java.io.InputStream;

/**
 * Command object representing a single file upload request.
 *
 * <p>This record encapsulates both file metadata and its binary content and is typically used as a
 * transport object between application layers.
 *
 * <p>Instances of this record are immutable with respect to their fields.
 * However, note that the {@link InputStream} itself may represent a mutable and consumable
 * resource.
 *
 * @param filename    the original name of the file; must not be {@code null}
 * @param size        the size of the file in bytes; must be greater than or equal to zero
 * @param contentType the MIME type of the file (e.g. {@code "application/pdf"},
 *                    {@code "image/png"}); may be {@code null} if unknown
 * @param inputStream the stream containing the file's binary data; must not be {@code null} and is
 *                    expected to be consumed once
 */
public record FileUploadCommand(
        String filename,
        long size,
        String contentType,
        InputStream inputStream
) {
}
