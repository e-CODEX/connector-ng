/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.model;

import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenTechnicalTrustLevel;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import java.security.cert.X509Certificate;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the data related to the diagnosis of a Connector token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisData {
    private XMLGregorianCalendar signingTime;
    private X509Certificate signingCertificate;
    private String signingCertificateIssuer;
    private String signingCertificateSubject;
    private String signatureFormatLevel;
    private SignatureQualification signatureConclusion;
    private X509Certificate issuerCertificate;
    private ConnectorTokenTechnicalTrustLevel trustLevel;
    private String comment;
}
