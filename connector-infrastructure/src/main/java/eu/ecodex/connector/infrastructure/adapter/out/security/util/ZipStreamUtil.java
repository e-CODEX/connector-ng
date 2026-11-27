/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.security.util;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.springframework.util.StringUtils;

/**
 * Utility class for working with ZIP streams.
 */
public class ZipStreamUtil {
    private static final int BUFFER_SIZE = 8192;

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
     * Extracts all entries from {@code zipDocument} as in-memory DSS documents, within the
     * {@link ZipLimits#DEFAULT default limits}.
     *
     * @param zipDocument the ZIP document to extract
     *
     * @return an ordered list of extracted entries
     *
     * @throws IOException              if a stream error occurs during extraction
     * @throws IllegalArgumentException if the document is not a ZIP, contains an unnamed or
     *                                  unsafe entry, or expands beyond the limits
     */
    public static List<DSSDocument> extract(DSSDocument zipDocument) throws IOException {
        return extract(zipDocument, ZipLimits.DEFAULT);
    }

    /**
     * Extracts all entries from {@code zipDocument} as in-memory DSS documents. The extraction
     * stops as soon as a limit is crossed: the sizes are those counted while reading, not those
     * announced by the ZIP headers, which cannot be trusted.
     *
     * @param zipDocument the ZIP document to extract
     * @param limits      the limits of the extraction
     *
     * @return an ordered list of extracted entries
     *
     * @throws IOException                       if a stream error occurs during extraction
     * @throws IllegalArgumentException          if the document is not a ZIP, or contains an
     *                                           unnamed or unsafe entry
     * @throws ConnectorZipLimitExceededException if the document expands beyond the limits
     */
    public static List<DSSDocument> extract(DSSDocument zipDocument, ZipLimits limits)
        throws IOException {
        if (!isZipFile(zipDocument)) {
            throw new IllegalArgumentException("The document in parameter is not in zip format!");
        }

        var documents = new ArrayList<DSSDocument>();
        long total = 0;

        try (var parentStream = zipDocument.openStream();
             var zipStream = new ZipInputStream(parentStream)) {

            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (documents.size() >= limits.maxEntries()) {
                    throw new ConnectorZipLimitExceededException(
                        "ZIP document contains more than %d entries"
                            .formatted(limits.maxEntries()));
                }
                checkName(entry.getName());
                var content = readEntry(zipStream, limits, total);
                total += content.length;
                documents.add(new InMemoryDocument(content, entry.getName()));
            }
        } catch (ZipException e) {
            throw new IllegalArgumentException(
                "Document '" + zipDocument.getName() + "' is not a valid ZIP file", e);
        }

        return documents;
    }

    private static void checkName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(
                "ZIP document contains an entry with an empty name");
        }
        var normalized = name.replace('\\', '/');
        if (normalized.startsWith("/") || Arrays.asList(normalized.split("/")).contains("..")) {
            throw new IllegalArgumentException(
                "ZIP document contains an entry with an unsafe name");
        }
    }

    private static byte[] readEntry(ZipInputStream zipStream, ZipLimits limits, long alreadyRead)
        throws IOException {
        var content = new ByteArrayOutputStream();
        var buffer = new byte[BUFFER_SIZE];
        long entrySize = 0;
        int read;
        while ((read = zipStream.read(buffer)) != -1) {
            entrySize += read;
            if (entrySize > limits.maxEntrySize()) {
                throw new ConnectorZipLimitExceededException(
                    "A ZIP entry is larger than %d bytes once expanded".formatted(
                        limits.maxEntrySize()));
            }
            if (alreadyRead + entrySize > limits.maxTotalSize()) {
                throw new ConnectorZipLimitExceededException(
                    "The ZIP entries are larger than %d bytes once expanded".formatted(
                        limits.maxTotalSize()));
            }
            content.write(buffer, 0, read);
        }
        return content.toByteArray();
    }
}
