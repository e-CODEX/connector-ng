/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.link;

import lombok.Getter;
import lombok.Setter;

/**
 * SOAP security configuration properties.
 *
 * <p>These properties define the key and trust material used for securing SOAP communication,
 * including keystores, private keys, and encryption settings.
 */
@Getter
@Setter
public class EndpointProperties {
    private KeystoreProperties keyStore;
    private PrivateKeyProperties privateKey;
    private KeystoreProperties trustStore;
    private String encryptAlias;
}
