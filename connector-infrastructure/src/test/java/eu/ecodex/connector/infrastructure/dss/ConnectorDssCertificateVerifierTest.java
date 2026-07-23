/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.dss;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.property.certificate.ConnectorCertificateVerifierProperties;
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorDssCertificateVerifierTest extends BaseDssTest {
    @Autowired
    private ConnectorDssCertificateVerifier certificateVerifier;

    @Test
    void should_create_common_certificate_verifier_successfully() {
        var truststore = new KeystoreProperties();
        truststore.setPath("classpath:keystores/connector-keystore.jks");
        truststore.setPassword("12345");
        truststore.setType(KeystoreType.JKS);

        var verifier = new ConnectorCertificateVerifierProperties();
        verifier.setTruststore(truststore);

        var commonCertificateVerifier = certificateVerifier.createCommonCertificateVerifier(
            verifier
        );

        assertThat(commonCertificateVerifier).isNotNull();
        assertThat(commonCertificateVerifier.getAIASource()).isNotNull();
    }

    @Test
    void should_throw_exception_creating_common_trusted_certificate_source_if_validation_is_malformed_1() {
        var verifier = new ConnectorCertificateVerifierProperties();
        assertThat(verifier).isNotNull();

        verifier.setIgnoreStoreEnabled(true);

        assertThrows(
            IllegalArgumentException.class,
            () -> certificateVerifier.createCommonCertificateVerifier(
                verifier
            )
        );
    }

    @Test
    void should_throw_exception_creating_common_trusted_certificate_source_if_validation_is_malformed_2() {
        var verifier = new ConnectorCertificateVerifierProperties();
        assertThat(verifier).isNotNull();

        assertThrows(
            IllegalArgumentException.class,
            () -> certificateVerifier.createCommonCertificateVerifier(
                verifier
            )
        );
    }
}
