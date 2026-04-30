/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.util;

import eu.europa.esig.dss.model.DSSDocument;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

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
     * @throws RuntimeException if an error occurs while reading the document stream
     */
    public static byte[] getDocumentData(final DSSDocument document) {
        InputStream in = null;
        try {
            in = document.openStream();

            return IOUtils.toByteArray(in);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
