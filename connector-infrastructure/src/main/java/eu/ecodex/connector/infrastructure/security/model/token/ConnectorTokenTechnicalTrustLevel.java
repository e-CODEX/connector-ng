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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Enumeration representing the technical trust level of a connector token.
 */
@Getter
@XmlEnum
@XmlType(name = "ConnectorTokenTechnicalTrustLevelEnum")
public enum ConnectorTokenTechnicalTrustLevel {
    /**
     * Aka RED.
     */
    @XmlEnumValue("FAIL")
    FAIL("FAIL", "Failed"),

    /**
     * Aka YELLOW.
     */
    @XmlEnumValue("SUFFICIENT")
    SUFFICIENT("SUFFICIENT", "Sufficient"),

    /**
     * Aka GREEN.
     */
    @XmlEnumValue("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL", "Successful");

    private final String value;
    private final String text;

    ConnectorTokenTechnicalTrustLevel(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
