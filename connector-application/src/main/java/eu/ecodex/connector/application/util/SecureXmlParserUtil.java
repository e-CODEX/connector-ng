/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.util;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for securely parsing XML strings into Document objects while mitigating security
 * risks.
 *
 * <p>This class ensures that XML parsing is performed with industry-standard security practices to
 * prevent common XML-based vulnerabilities, such as XML External Entity (XXE) attacks. By
 * leveraging secure configurations of the {@link DocumentBuilderFactory}, the class addresses
 * potential risks associated with processing untrusted XML content, such as denial-of-service
 * attacks or unauthorized data exposure.
 */
@Slf4j
public class SecureXmlParserUtil {
    /**
     * Parses an XML string into a {@link Document} object securely, mitigating common XML-based
     * vulnerabilities such as XML External Entity (XXE) attacks. This method ensures that XML
     * parsing is performed by disabling features prone to abuse and applying best practices for
     * secure XML handling.
     *
     * @param xmlString The XML content to be parsed, provided as a string.
     *
     * @return A {@link Document} object representing the parsed XML content.
     * @throws RuntimeException If an error occurs during XML parsing or secure configuration
     *                          setup.
     */
    public static Document parseSecurely(String xmlString) {
        log.debug("parsing XML string: {}", xmlString);
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Prevent XXE (XML External Entity) attacks
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            log.error("error configuring XML parser ", e);
            throw new RuntimeException(e);
        }

        // disable XInclude
        factory.setXIncludeAware(false);

        // disable entity expansion
        factory.setExpandEntityReferences(false);

        // limit entity expansion (if available in your Java version)
        try {
            factory.setAttribute(
                    "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "0");
        } catch (IllegalArgumentException e) {
            log.warn("entity expansion limit not supported", e);
        }

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            var parsedXml = builder.parse(new InputSource(new StringReader(xmlString)));

            log.debug("XML parsed successfully");

            return parsedXml;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("error parsing XML string", e);

            throw new RuntimeException(e);
        }
    }
}
