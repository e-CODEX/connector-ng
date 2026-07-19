/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.model.token.signature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the certificate information associated with a connector token.
 */
@Setter
@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ConnectorTokenCertificateInformationType",
    propOrder = {"subject", "issuer", "valid", "validityAtSigningTime"}
)
public class ConnectorTokenCertificateInformation implements Serializable {
    @XmlElement(name = "Subject")
    private String subject;
    @XmlElement(name = "Issuer", required = true)
    private String issuer;
    @XmlElement(name = "CertificateVerification")
    private boolean valid;
    @XmlElement(name = "ValidityAtSigningTime")
    private boolean validityAtSigningTime;
}
