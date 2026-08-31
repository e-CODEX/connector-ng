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
import eu.europa.esig.dss.spi.DSSUtils;
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
     */
    public static byte[] getDocumentData(DSSDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }

        return DSSUtils.toByteArray(document.openStream());
    }
}
