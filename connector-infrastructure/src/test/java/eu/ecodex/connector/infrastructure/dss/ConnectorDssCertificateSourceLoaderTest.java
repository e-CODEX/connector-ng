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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.europa.esig.dss.enumerations.CertificateSourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ConnectorDssCertificateSourceLoader")
public class ConnectorDssCertificateSourceLoaderTest extends BaseDssTest {
    @Autowired
    private ConnectorDssCertificateSourceLoader certificateSourceLoader;

    private KeystoreProperties keystoreProperties(
        String path,
        KeystoreType type
    ) {
        var properties = new KeystoreProperties();
        properties.setPath(path);
        properties.setPassword("12345");
        properties.setType(type);
        return properties;
    }

    @Nested
    @DisplayName("certificate source")
    class CertificateSource {
        @Test
        void should_create_certificate_source() {
            var properties = keystoreProperties(
                "classpath:keystores/connector-keystore.jks",
                KeystoreType.JKS
            );

            var source = certificateSourceLoader.createCertificateSource(properties);

            assertThat(source).isNotNull();

            var token = source.getCertificate("connector_blue");

            assertThat(token).isNotNull();
            assertThat(token.isSelfSigned()).isTrue();
        }

        @Test
        void should_fail_when_keystore_path_is_null() {
            var properties = keystoreProperties(
                null,
                KeystoreType.PKCS12
            );

            assertThatThrownBy(
                () -> certificateSourceLoader.createCertificateSource(properties)
            )
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_fail_when_keystore_path_is_empty() {
            var properties = keystoreProperties("", KeystoreType.PKCS12);

            assertThatThrownBy(
                () -> certificateSourceLoader.createCertificateSource(properties)
            )
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_fail_when_keystore_path_is_invalid() {
            var properties = keystoreProperties(
                "classpath:keystores/unknown-keystore.jks",
                KeystoreType.PKCS12
            );

            assertThatThrownBy(
                () -> certificateSourceLoader.createCertificateSource(properties)
            )
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("common trusted certificate source")
    class CommonTrustedCertificateSource {
        @Test
        void should_create_trusted_certificate_source() {
            var properties = keystoreProperties(
                "classpath:keystores/connector-keystore.jks",
                KeystoreType.JKS
            );

            var trustedCertificateSource =
                certificateSourceLoader.createCommonTrustedCertificateSource(properties);

            assertThat(trustedCertificateSource).isNotNull();
            assertThat(trustedCertificateSource.getCertificateSourceType())
                .isEqualTo(CertificateSourceType.TRUSTED_STORE);

            var token = certificateSourceLoader
                .createCertificateSource(properties)
                .getCertificate("connector_blue");

            assertThat(trustedCertificateSource.isTrusted(token)).isTrue();
        }
    }
}

