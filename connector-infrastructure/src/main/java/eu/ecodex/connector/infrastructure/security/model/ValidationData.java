/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model;

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the data related to the validation of a Connector token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationData {
    private boolean signatureComputation;
    private boolean signatureConclusion;
    private boolean signatureFormat;
    private ConnectorTokenTechnicalTrustLevel signatureCertStatus;
    private ConnectorTokenTechnicalTrustLevel signatureCertHistory;
    private boolean trustAnchor;
    private ConnectorTokenTechnicalTrustLevel issuerCertStatus;
    private ConnectorTokenTechnicalTrustLevel issuerCertHistory;
}
