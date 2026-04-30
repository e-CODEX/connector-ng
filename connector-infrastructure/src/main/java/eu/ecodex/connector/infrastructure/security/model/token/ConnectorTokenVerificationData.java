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

import eu.ecodex.connector.infrastructure.security.model.token.signature.ConnectorTokenSignature;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds the data used during the verification of a connector token.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ConnectorTokenVerificationDataType",
        propOrder = {"signatureData", "authenticationData"}
)
public class ConnectorTokenVerificationData {
    @XmlElement(name = "SignatureData")
    private List<ConnectorTokenSignature> signatureData;
    @XmlElement(name = "AuthenticationData")
    private ConnectorTokenAuthenticationData authenticationData;

    /**
     * Adds a signature data object to the list of existing signature data.
     *
     * @param signatureData the {@code ConnectorTokenSignature} object representing the signature
     *                      data to be added to the list. Cannot be null.
     */
    public void addSignatureData(ConnectorTokenSignature signatureData) {
        if (this.signatureData == null) {
            this.signatureData = new ArrayList<>();
        }

        if (signatureData != null) {
            this.signatureData.add(signatureData);
        }
    }
}
