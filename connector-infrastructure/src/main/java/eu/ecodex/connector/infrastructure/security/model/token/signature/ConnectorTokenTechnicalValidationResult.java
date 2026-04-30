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

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenTechnicalTrustLevel;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the technical validation result of a connector token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConnectorTokenTechnicalValidationResultType",
        propOrder = {"trustLevel", "comment"}
)
public final class ConnectorTokenTechnicalValidationResult {
    @XmlElement(name = "TrustLevel", required = true)
    private ConnectorTokenTechnicalTrustLevel trustLevel;
    @XmlElement(name = "Comments")
    private String comment;
}
