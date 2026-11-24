/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.helper;

import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * Builds the headers of the file downloads from values which may come from outside of the
 * connector, such as the name and the content type of an uploaded attachment.
 */
public final class ConnectorDownloadHeaders {
    private static final String DEFAULT_FILENAME = "download";

    private ConnectorDownloadHeaders() {
    }

    /**
     * Builds the value of a {@code Content-Disposition} header asking to save the file under the
     * given name. The name is quoted and, when needed, encoded: separators, quotes and non-ASCII
     * characters cannot alter the header.
     *
     * @param filename the name of the file; {@code download} is used if it is blank
     *
     * @return the header value
     */
    public static String attachment(String filename) {
        var name = StringUtils.hasText(filename) ? filename : DEFAULT_FILENAME;

        return ContentDisposition.attachment()
                                 .filename(name, StandardCharsets.UTF_8)
                                 .build()
                                 .toString();
    }

    /**
     * Parses a content type, falling back to {@code application/octet-stream} when it is missing
     * or malformed.
     *
     * @param contentType the content type to parse
     *
     * @return the media type, never {@code null}
     */
    public static MediaType mediaType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
