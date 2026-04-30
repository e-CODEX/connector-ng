/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.evidence;

import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.ecodex.connector.infrastructure.property.common.PrivateKeyProperties;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Keystore for signing REM evidences and digest for hashing business payload bytes.
 */
@Getter
@Setter
public class EvidencesSignatureProperties {

    @NotNull
    private DigestAlgorithm payloadDigestAlgorithm = DigestAlgorithm.SHA256;

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private KeystoreProperties keystore = new KeystoreProperties();

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private PrivateKeyProperties privateKey = new PrivateKeyProperties();
}
