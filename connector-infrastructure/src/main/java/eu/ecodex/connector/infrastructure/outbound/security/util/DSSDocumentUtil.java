/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.util;

import eu.europa.esig.dss.model.DSSDocument;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for handling {@link DSSDocument} operations.
 */
public class DSSDocumentUtil {
    /**
     * Reads the full content of a {@link DSSDocument} and returns it as a byte array.
     *
     * <p>This method opens an {@link InputStream} from the provided document,
     * reads all bytes using Apache Commons IO utilities, and ensures the stream is closed quietly
     * afterward.
     *
     * @param document the DSSDocument to read from (must not be null)
     *
     * @return a byte array containing the full content of the document
     *
     * @throws RuntimeException if an error occurs while reading the document stream
     */
    public static byte[] getDocumentData(DSSDocument document) throws IOException {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }

        try (var in = document.openStream()) {
            return in.readAllBytes();
        }
    }

    /**
     * Returns {@code true} if {@code document} contains at least one byte of data.
     *
     * @param document the document to probe (must not be null)
     *
     * @return {@code true} if data is present, {@code false} if the document is empty or the stream
     *     cannot be opened
     */
    public static boolean hasData(DSSDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }

        try (var inputStream = document.openStream()) {
            return inputStream.read() != -1;
        } catch (Exception e) {
            return false;
        }
    }
}
