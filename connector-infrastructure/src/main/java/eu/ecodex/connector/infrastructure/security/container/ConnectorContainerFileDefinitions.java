/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container;

/**
 * Provides definitions for various file paths and names used in a connector container.
 */
public interface ConnectorContainerFileDefinitions {
    /**
     * The name of the signed content ZIP.
     */
    FileDef SIGNED_CONTENT = new FileDef(null, "SignedContent.zip");
    String SIGNED_CONTENT_REF = SIGNED_CONTENT.reference();
    /**
     * The name of the PDF TrustOkToken, inside the inner ZIP.
     */
    FileDef TOKEN_PDF = new FileDef(null, "TrustOkToken.pdf");
    String TOKEN_PDF_REF = TOKEN_PDF.reference();
    /**
     * The name of the XML TrustOkToken.
     */
    FileDef TOKEN_XML = new FileDef("META-INF", "trustOkToken.xml");
    String TOKEN_XML_REF = TOKEN_XML.reference();
    /**
     * The path of the XML signatures, inside the ZIP.
     */
    FileDef SIGNATURES = new FileDef("META-INF", "signatures.xml");
    String SIGNATURES_REF = SIGNATURES.reference();
    /**
     * The name of the Asic container.
     */
    FileDef SIGNED_CONTENT_ASIC = new FileDef(null, "SignedContent.zip.ASIC");
    String SIGNED_CONTENT_ASIC_REF = SIGNED_CONTENT_ASIC.reference();

    /**
     * Represents the definition of a file with its location and name. Provides functionality to
     * normalize input and generate a complete reference combining the location and name.
     */
    record FileDef(String location, String name) {
        public FileDef {
            location = clean(location);
            name = clean(name);
        }

        private static String clean(String s) {
            if (s == null) {
                return "";
            }
            return s.strip().replaceAll("^/+|/+$", "");
        }

        public String reference() {
            var ref = location.isEmpty() ? "" : location + "/";
            return ref + name;
        }
    }
}
