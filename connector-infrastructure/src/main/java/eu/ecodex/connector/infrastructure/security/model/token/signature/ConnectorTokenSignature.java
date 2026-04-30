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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a digital signature within a connector token context.
 */
@Setter
@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        name = "ConnectorTokenSignatureType",
        propOrder = {
                "signingTime", "signatureInformation", "certificateInformation", "technicalResult"
        }
)
public class ConnectorTokenSignature {
    @XmlElement(name = "SigningTime")
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar signingTime;
    @XmlElement(name = "SignatureInformation")
    private ConnectorTokenSignatureInformation signatureInformation;
    @XmlElement(name = "CertificateInformation")
    private ConnectorTokenCertificateInformation certificateInformation;
    @XmlElement(name = "TechnicalResult")
    private ConnectorTokenTechnicalValidationResult technicalResult;
    private ConnectorTokenAuthenticationCertificate authenticationCertificate;
}
