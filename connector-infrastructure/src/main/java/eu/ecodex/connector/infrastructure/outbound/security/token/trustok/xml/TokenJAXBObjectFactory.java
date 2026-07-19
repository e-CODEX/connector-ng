/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.token.trustok.xml;

import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import lombok.NoArgsConstructor;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the eu.ecodex.connector.infrastructure.security.container.model.token package.
 *
 * <p>An ObjectFactory allows you to programmatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of
 * schema-derived interfaces and classes representing the binding of schema type definitions,
 * element declarations, and model groups. Factory methods for each of these are provided in this
 * class.
 */
@XmlRegistry
@NoArgsConstructor
public class TokenJAXBObjectFactory {
    private static final QName _TrustOkToken_QNAME = new QName("", "TrustOkToken");

    /**
     * Create an instance of {@link JAXBElement }{@code &lt;}{@link ConnectorToken}{@code &gt;}.
     *
     * @param value the token
     *
     * @return the new instance representing the marshalled object
     */
    @XmlElementDecl(namespace = "", name = "TrustOkToken")
    public JAXBElement<ConnectorToken> createTrustOkToken(final ConnectorToken value) {
        return new JAXBElement<>(_TrustOkToken_QNAME, ConnectorToken.class, null, value);
    }
}
