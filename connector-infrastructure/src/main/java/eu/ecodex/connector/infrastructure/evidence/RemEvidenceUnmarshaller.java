/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import eu.spocseu.edeliverygw.JaxbContextHolder;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Parses signed REM evidence XML into {@link REMEvidenceType}.
 */
public final class RemEvidenceUnmarshaller {

    private RemEvidenceUnmarshaller() {
    }

    /**
     * Unmarshals signed REM evidence XML bytes to {@link REMEvidenceType}.
     *
     * @param xmlData signed evidence document
     *
     * @return parsed evidence root
     */
    public static REMEvidenceType parseSignedEvidenceXml(byte[] xmlData) {
        var builderFactory = DocumentBuilderFactory.newInstance();
        try {
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
        builderFactory.setNamespaceAware(true);

        try {
            Document doc = builderFactory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlData));

            JAXBElement<REMEvidenceType> jaxbObj = convertIntoEvidenceElement(doc);

            REMEvidenceType value = jaxbObj != null ? jaxbObj.getValue() : null;
            if (value == null) {
                throw new IllegalStateException(
                        "failed to parse REM evidence: unmarshaller returned null"
                );
            }
            return value;
        } catch (IOException | SAXException | ParserConfigurationException | JAXBException e) {
            throw new IllegalStateException("failed to parse REM evidence document", e);
        }
    }

    private static JAXBElement<REMEvidenceType> convertIntoEvidenceElement(Document domDocument)
            throws JAXBException {
        return JaxbContextHolder.getSpocsJaxBContext()
                .createUnmarshaller()
                .unmarshal(domDocument, REMEvidenceType.class);
    }
}
