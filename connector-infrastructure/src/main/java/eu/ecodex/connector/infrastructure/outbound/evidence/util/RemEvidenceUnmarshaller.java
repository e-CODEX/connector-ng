/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.util;

import eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.JaxbContextHolder;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Parses signed REM evidence XML into {@link REMEvidenceType}.
 */
public final class RemEvidenceUnmarshaller {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY =
        createDocumentBuilderFactory();

    private RemEvidenceUnmarshaller() {
    }

    /**
     * Unmarshals signed REM evidence XML bytes to {@link REMEvidenceType}.
     *
     * @param xmlData signed evidence document (must not be null)
     *
     * @return parsed evidence root
     *
     * @throws IllegalStateException if the document cannot be parsed or unmarshalled
     */
    public static REMEvidenceType parseSignedEvidenceXml(byte[] xmlData) {
        Objects.requireNonNull(xmlData, "xmlData must not be null");

        try {
            var doc = DOCUMENT_BUILDER_FACTORY
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xmlData));

            return convertIntoEvidenceElement(doc).getValue();
        } catch (IOException | SAXException | ParserConfigurationException | JAXBException e) {
            throw new IllegalStateException("Failed to parse REM evidence document", e);
        }
    }

    private static JAXBElement<REMEvidenceType> convertIntoEvidenceElement(Document document)
        throws JAXBException {
        return JaxbContextHolder.getSpocsJaxBContext()
                                .createUnmarshaller()
                                .unmarshal(document, REMEvidenceType.class);
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }

        return factory;
    }
}
