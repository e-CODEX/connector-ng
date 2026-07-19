/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.soap.helper;

import jakarta.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.experimental.UtilityClass;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.util.StringUtils;

@UtilityClass
@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class AttachmentHelpers {
    private static final TransformerFactory transformerFactory = getTransformerFactory();

    public static Path sourceToTempFile(Source source) throws Exception {
        var sourceFileName = StringUtils.cleanPath(getSourceFileName(source));
        var tempFile = Files.createTempFile(
            "ws-payload-", !StringUtils.hasText(sourceFileName) ? "default.tmp" : sourceFileName
        );
        try {
            var transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            var xmlOutput = new StreamResult(new ByteArrayOutputStream());
            transformer.transform(source, xmlOutput);

            var bos = (ByteArrayOutputStream) xmlOutput.getOutputStream();

            Files.write(tempFile, bos.toByteArray());

            return tempFile;
        } catch (IllegalArgumentException | TransformerException e) {
            throw new RuntimeException("Exception occurred during transforming xml into byte[]", e);
        }
    }

    public static Path dataHandlerToTempFile(DataHandler dh) throws Exception {
        var ext = contentTypeToExtension(dh.getContentType());
        var tempFile = Files.createTempFile("ws-attachment-%s".formatted(UUID.randomUUID()), ext);

        try (var inputStream = dh.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return tempFile;
    }

    private static TransformerFactory getTransformerFactory() {
        TransformerFactory transformerFactory;

        try {
            transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error setting TransformerFactory attribute", e);
        }

        return transformerFactory;
    }

    private String contentTypeToExtension(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return ".tmp";
        }

        var baseType = contentType.split(";")[0].trim().toLowerCase();
        var mimeTypes = MimeTypes.getDefaultMimeTypes();

        try {
            var mimeType = mimeTypes.forName(baseType);
            var extension = mimeType.getExtension();

            return extension == null ? ".tmp" : extension;
        } catch (MimeTypeException e) {
            return ".tmp";
        }
    }

    public static String getSourceFileName(Source source) {
        if (source == null) {
            return null;
        }

        String systemId = source.getSystemId();
        if (systemId != null) {
            try {
                return Paths.get(new URI(systemId)).getFileName().toString();
            } catch (URISyntaxException | IllegalArgumentException e) {
                // Not a file URI, return as-is
                return systemId;
            }
        }

        // StreamSource: try to extract filename from underlying stream/reader
        if (source instanceof StreamSource ss) {
            // Built from a File or path — system ID should have caught this,
            // but try a direct cast in case setSystemId() was never called
            var is = ss.getInputStream();
            if (is instanceof FileInputStream fis) {
                try {
                    // Reflect to get the file path from FileDescriptor (best effort)
                    var pathField = FileInputStream.class.getDeclaredField("path");
                    pathField.setAccessible(true);
                    var path = (String) pathField.get(fis);

                    if (path != null) {
                        return Paths.get(path).getFileName().toString();
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // JVM doesn't expose it, give up
                }
            }

            // Reader fallback: FileReader wraps a FileInputStream internally
            var reader = ss.getReader();
            if (reader instanceof FileReader fr) {
                // FileReader exposes the path since Java 11 via getEncoding(),
                // but the actual path is only available via reflection
                try {
                    var inField = InputStreamReader.class.getDeclaredField("in");
                    inField.setAccessible(true);
                    Object streamDecoder = inField.get(fr); // sun.nio.cs.StreamDecoder
                    var sourceField = streamDecoder.getClass().getDeclaredField("cs");
                    // Not reliable — FileReader path not officially exposed before Java 17+
                } catch (Exception e) {
                    // Ignore
                }
                // Java 17+ alternative: fr.getPath() does NOT exist on FileReader
                // Best we can do is give up here
            }
        }

        // SAXSource: delegate to InputSource
        if (source instanceof SAXSource ss) {
            var inputSource = ss.getInputSource();
            if (inputSource != null && inputSource.getSystemId() != null) {
                try {
                    return Paths.get(new URI(inputSource.getSystemId())).getFileName().toString();
                } catch (URISyntaxException | IllegalArgumentException e) {
                    return inputSource.getSystemId();
                }
            }
        }
        // DOMSource: no filename info available beyond systemId (already checked)
        // StAXSource: getSystemId() always returns null, nothing to do

        return null;
    }
}
