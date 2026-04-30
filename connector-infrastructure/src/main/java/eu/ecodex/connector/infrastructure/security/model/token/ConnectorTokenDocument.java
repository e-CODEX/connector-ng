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

import eu.europa.esig.xmldsig.jaxb.DigestMethodType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a document associated with a connector token.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConnectorTokenDocumentType",
        propOrder = {"filename", "type", "digestMethod", "digestValue", "signatureFilename"}
)
public final class ConnectorTokenDocument implements Serializable {
    @XmlElement(name = "Filename", required = true)
    private String filename;
    @XmlElement(name = "Type", required = true)
    private String type;
    @XmlElement(
            name = "DigestMethod", required = true, namespace = "http://www.w3.org/2000/09/xmldsig#"
    )
    private DigestMethodType digestMethod;
    @XmlElement(
            name = "DigestValue", required = true, namespace = "http://www.w3.org/2000/09/xmldsig#"
    )
    private byte[] digestValue;
    @XmlElement(name = "SignatureFilename")
    private String signatureFilename;
}
