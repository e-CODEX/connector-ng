/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model.token;

import eu.europa.esig.dss.validation.reports.Reports;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;

/**
 * Represents the original validation report container of a connector token.
 */
@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConnectorTokenOriginalValidationReportContainerType", propOrder = {"any"})
public class ConnectorTokenOriginalValidationReportContainer {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY
            = DocumentBuilderFactory.newInstance();
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlTransient
    private Reports reports;

    /**
     * Retrieves the list of elements stored in the "any" property. If the list is not initialized,
     * a new instance is created and returned.
     *
     * @return a mutable list of {@code Object} elements; never null
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }

        return any;
    }

    /**
     * Assigns a {@link Reports} object to the current instance and extracts specific diagnostic
     * data and simple report data from it. The extracted data is then added as entries to the "any"
     * property of this instance.
     *
     * @param reports the {@link Reports} object containing diagnostic and report data
     */
    public void setReports(Reports reports) {
        this.reports = reports;
        // uncomment when necessary.
        // addXmlString(reports.getXmlDiagnosticData());
        addXmlString(reports.getXmlSimpleReport());
    }

    private void addXmlString(String xmlContent) {
        if (!StringUtils.hasText(xmlContent)) {
            return;
        }

        try {
            DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
            var doc = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder()
                                              .parse(new InputSource(new StringReader(xmlContent)));
            this.getAny().add(doc.getDocumentElement());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse XML report into DOM element", e
            );
        }
    }

    /**
     * Represents a simple wrapper for a single value of any type, providing serialization support
     * for XML-based contexts.
     */
    @XmlRootElement
    @NoArgsConstructor
    public static class SimpleTypeEntry {
        /**
         * The wrapped value (e.g. String et al.).
         */
        @XmlElement
        public Object value;

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
