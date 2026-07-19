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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

/**
 * Enumeration of AES (Advanced Electronic Signature) token types used in the connector.
 */
@Getter
@XmlEnum
@XmlType(name = "AdvancedSystemEnum")
public enum ConnectorTokenAESType {
    @XmlEnumValue("Authentication-based")
    AUTHENTICATION_BASED("Authentication-based", "Authentication-based"),
    @XmlEnumValue("Signature-based")
    SIGNATURE_BASED("Signature-based", "Signature-based");

    private final String value;
    private final String text;

    ConnectorTokenAESType(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
