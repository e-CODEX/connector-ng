/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystoreType;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class KeystoreTestFixtures {
    public static ConnectorKeystore createKeystore() {
        return ConnectorKeystore
                .builder()
                .content(new byte[1])
                .description("test keystore")
                .password("121345")
                .type(ConnectorKeystoreType.JKS)
                .filename("truststore.jks")
                .build();
    }
}
