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
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.europa.esig.dss.enumerations.CertificateSourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorDssCertificateSourceLoaderTest extends BaseDssTest {
    @Autowired
    private ConnectorDssCertificateSourceLoader certificateSourceLoader;

    // certificate source

    @Test
    void should_create_certificate_source_successfully() {
        var properties = new KeystoreProperties();
        properties.setPath("classpath:keystores/connector-keystore.jks");
        properties.setPassword("12345");
        properties.setType(KeystoreType.JKS);

        var source = certificateSourceLoader.createCertificateSource(properties);
        assertThat(source).isNotNull();

        var token = source.getCertificate("connector_blue");

        assertThat(token).isNotNull();
        assertThat(token.isSelfSigned()).isTrue();
    }

    @Test
    void should_fail_to_create_certificate_source_if_path_is_null() {
        var properties = new KeystoreProperties();
        properties.setPath(null);
        properties.setPassword("12345");
        properties.setType(KeystoreType.PKCS12);

        assertThrows(
            IllegalArgumentException.class,
            () -> certificateSourceLoader.createCertificateSource(properties)
        );
    }

    @Test
    void should_fail_to_create_certificate_source_if_path_is_empty() {
        var properties = new KeystoreProperties();
        properties.setPath("");
        properties.setPassword("12345");
        properties.setType(KeystoreType.PKCS12);

        assertThrows(
            IllegalArgumentException.class,
            () -> certificateSourceLoader.createCertificateSource(properties)
        );
    }

    @Test
    void should_fail_to_create_certificate_source_if_path_is_invalid() {
        var properties = new KeystoreProperties();
        properties.setPath("classpath:keystores/unknown-keystore.jks");
        properties.setPassword("12345");
        properties.setType(KeystoreType.PKCS12);

        assertThrows(
            IllegalStateException.class,
            () -> certificateSourceLoader.createCertificateSource(properties)
        );
    }

    // common trusted source

    @Test
    void should_create_common_trusted_certificate_source_successfully() {
        var properties = new KeystoreProperties();
        properties.setPath("classpath:keystores/connector-keystore.jks");
        properties.setPassword("12345");
        properties.setType(KeystoreType.JKS);

        var trustedCertificateSource = certificateSourceLoader.createCommonTrustedCertificateSource(
            properties);
        assertThat(trustedCertificateSource).isNotNull();
        assertThat(trustedCertificateSource.getCertificateSourceType()).isEqualTo(
            CertificateSourceType.TRUSTED_STORE);

        var token = certificateSourceLoader.createCertificateSource(properties).getCertificate(
            "connector_blue");

        assertThat(trustedCertificateSource.isTrusted(token)).isTrue();
    }
}

