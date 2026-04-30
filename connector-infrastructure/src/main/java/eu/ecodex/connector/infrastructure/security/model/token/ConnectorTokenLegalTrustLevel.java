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
 * Enumeration of legal trust levels used in the connector.
 */
@Getter
@XmlEnum
@XmlType(name = "LegalTrustLevelEnum")
public enum ConnectorTokenLegalTrustLevel {
    @XmlEnumValue("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL", "Successful"),
    @XmlEnumValue("UNDETERMINED")
    UNDETERMINED("UNDETERMINED", "Undetermined"),
    @XmlEnumValue("NOT_SUCCESSFUL")
    NOT_SUCCESSFUL("NOT_SUCCESSFUL", "Not Successful");

    private final String value;
    private final String text;

    ConnectorTokenLegalTrustLevel(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
