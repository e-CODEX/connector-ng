/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property.certificate;

import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the certificate verifier.
 */
@Getter
@Setter
@Configuration
public class ConnectorCertificateVerifierProperties {
    /**
     * should ocsp be queried?.
     */
    private final boolean ocspEnabled = true;
    /**
     * should a crl be queried?.
     */
    private final boolean crlEnabled = true;
    /**
     * should AIA be used?.
     */
    private final boolean aiaEnabled = true;
    private boolean truststoreEnabled = true;
    private boolean ignoreStoreEnabled = false;
    private String trustedListSource;
    private KeystoreProperties truststore;
    private KeystoreProperties ignoreStore;
}
