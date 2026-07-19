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
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.springframework.util.StringUtils;

/**
 * Utility class for working with ZIP streams.
 */
public class ZipStreamUtil {
    /**
     * Checks if the given DSSDocument represents a valid ZIP file based on its magic number.
     *
     * @param zipDocument the document to check must not be null
     *
     * @return true if the document is a ZIP file, false otherwise
     *
     * @throws IllegalArgumentException if the provided document is null
     */
    public static boolean isZipFile(DSSDocument zipDocument) {
        if (zipDocument == null) {
            throw new IllegalArgumentException("zipDocument must not be null");
        }

        try (var inputStream = zipDocument.openStream();
             var buffered = new BufferedInputStream(inputStream);
             var data = new DataInputStream(buffered)
        ) {
            return data.readInt() == 0x504B0304;
        } catch (EOFException e) {
            // Document is shorter than 4 bytes — cannot be a ZIP
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts all entries from {@code zipDocument} as in-memory DSS documents.
     *
     * @param zipDocument the ZIP document to extract
     *
     * @return an ordered list of extracted entries
     *
     * @throws IOException              if a stream error occurs during extraction
     * @throws IllegalArgumentException if the document is not a ZIP or contains an unnamed entry
     */
    public static List<DSSDocument> extract(DSSDocument zipDocument) throws IOException {
        if (!isZipFile(zipDocument)) {
            throw new IllegalArgumentException("The document in parameter is not in zip format!");
        }

        var documents = new ArrayList<DSSDocument>();

        try (var parentStream = zipDocument.openStream();
             var zipStream = new ZipInputStream(parentStream)) {

            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (!StringUtils.hasText(entry.getName())) {
                    throw new IllegalArgumentException(
                        "ZIP document contains an entry with an empty name");
                }
                documents.add(new InMemoryDocument(
                    zipStream.readAllBytes(), entry.getName()));
            }
        } catch (ZipException e) {
            throw new IllegalArgumentException(
                "Document '" + zipDocument.getName() + "' is not a valid ZIP file", e);
        }

        return documents;
    }
}
