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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class AttachmentHelpers {
    private static final TransformerFactory transformerFactory = getTransformerFactory();

    public static Path sourceToTempFile(Source source) throws Exception {
        var tempFile = Files.createTempFile("ws-payload-", ".xml");
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

    public static String sourceToBytes(Source source) throws Exception {
        var path = sourceToTempFile(source);
        return Files.readString(path);
    }

    public static Path dataHandlerToTempFile(DataHandler dh) throws Exception {
        var ext = contentTypeToExtension(dh.getContentType());
        var tempFile = Files.createTempFile("ws-attachment-", ext);

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

        return switch (baseType) {
            case "application/pdf"              -> ".pdf";
            case "text/xml",
                 "application/xml"             -> ".xml";
            case "application/json"            -> ".json";
            case "application/zip"             -> ".zip";
            case "application/octet-stream"    -> ".bin";
            case "text/plain"                  -> ".txt";
            case "image/jpeg"                  -> ".jpg";
            case "image/png"                   -> ".png";
            default                            -> ".tmp";
        };
    }
}
