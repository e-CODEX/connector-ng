/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import eu.ecodex.connector.evidences.HashValueBuilder;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssSigningTokenProvider;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans for REM evidence signing (keystore-backed DSS token) and payload hashing.
 */
@Configuration
@EnableConfigurationProperties(ConnectorEvidencesProperties.class)
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class EvidenceInfrastructureConfig {

    public static final String REM_EVIDENCE_SIGNING_TOKEN_BEAN = "connectorRemEvidenceSigningTokenProvider";

    @Bean(name = REM_EVIDENCE_SIGNING_TOKEN_BEAN, destroyMethod = "close")
    public ConnectorDssSigningTokenProvider connectorRemEvidenceSigningTokenProvider(
            ConnectorEvidencesProperties properties) {
        return new ConnectorDssSigningTokenProvider(
                properties.getSignature().getKeystore(),
                properties.getSignature().getPrivateKey()
        );
    }

    @Bean
    public HashValueBuilder evidencePayloadHashValueBuilder(
            ConnectorEvidencesProperties properties) {
        return new HashValueBuilder(properties.getSignature().getPayloadDigestAlgorithm());
    }
}
