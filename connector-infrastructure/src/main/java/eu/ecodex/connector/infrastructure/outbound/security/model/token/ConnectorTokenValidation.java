/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.model.token;

import eu.ecodex.connector.infrastructure.outbound.security.model.token.signature.ConnectorTokenTechnicalValidationResult;
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
 * Represents the validation result of a connector token.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ConnectorTokenValidationType",
    propOrder = {
        "verificationTime",
        "verificationData",
        "technicalResult",
        "legalResult",
        "originalValidationReport"
    }
)
public class ConnectorTokenValidation {
    @XmlSchemaType(name = "dateTime")
    @XmlElement(name = "VerificationTime", required = true)
    private XMLGregorianCalendar verificationTime;
    @XmlElement(name = "VerificationData", required = true)
    private ConnectorTokenVerificationData verificationData;
    @XmlElement(name = "TechnicalResult", required = true)
    private ConnectorTokenTechnicalValidationResult technicalResult;
    @XmlElement(name = "LegalResult", required = true)
    private ConnectorTokenLegalValidationResult legalResult;
    @XmlElement(name = "OriginalValidationReport")
    private ConnectorTokenOriginalValidationReportContainer originalValidationReport;
}
