/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model.token.signature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains metadata and validation results related to a digital signature.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConnectorTokenSignatureInformationType",
        propOrder = {"signatureValid", "structureValid", "format", "level"}
)
public class ConnectorTokenSignatureInformation {
    @XmlElement(name = "SignatureVerification")
    private boolean signatureValid;
    @XmlElement(name = "StructureVerification")
    private boolean structureValid;
    @XmlElement(name = "SignatureFormat", required = true)
    private String format;
    @XmlElement(name = "SignatureLevel")
    private String level;
}
