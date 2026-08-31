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

import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.infrastructure.property.common.KeystoreProperties;
import eu.ecodex.connector.infrastructure.util.ResourceStreams;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Loader responsible for creating DSS certificate sources from keystore configuration.
 *
 * <p>Supports loading keystores from:
 * <ul>
 *     <li>Classpath resources (using the {@code classpath:} prefix)
 *     <li>Remote URLs
 *     <li>Local file system paths
 * </ul>
 */
@Slf4j
@Component
public class ConnectorDssCertificateSourceLoader {
    /**
     * Creates a {@link KeyStoreCertificateSource} from the given keystore configuration.
     *
     * @param keystore the keystore properties containing a path, type, and password
     *
     * @return a {@link KeyStoreCertificateSource} initialized from the provided keystore
     *
     * @throws IllegalStateException if the keystore cannot be loaded
     */
    public KeyStoreCertificateSource createCertificateSource(KeystoreProperties keystore) {
        try (var keystoreStream = ResourceStreams.openStream(keystore.getPath())) {
            return new KeyStoreCertificateSource(
                keystoreStream,
                keystore.getType().name(),
                keystore.getPassword().toCharArray()
            );
        } catch (IOException e) {
            log.error("Unable to load truststore from [{}]", keystore);
            throw new IllegalStateException("Failed to load application keystore", e);
        }
    }

    /**
     * Creates a {@link KeyStoreCertificateSource} from the given truststore configuration.
     *
     * @param truststore the truststore configuration containing filename, content, password, and
     *                   type of the truststore
     *
     * @return a {@link KeyStoreCertificateSource} initialized from the provided truststore
     *
     * @throws IllegalStateException if the truststore cannot be loaded
     */
    public KeyStoreCertificateSource createCertificateSource(ConnectorTruststore truststore) {
        try (var stream = new ByteArrayInputStream(truststore.content())) {
            return new KeyStoreCertificateSource(
                stream,
                truststore.type().name(),
                truststore.password().toCharArray()
            );
        } catch (IOException e) {
            log.error("Unable to load truststore [{}] from the database", truststore.filename());
            throw new IllegalStateException("Failed to load message truststore", e);
        }
    }

    /**
     * Creates a {@link CommonTrustedCertificateSource} using the provided keystore.
     *
     * <p>All certificates from the keystore are imported and marked as trusted.
     *
     * @param keystore the keystore properties
     *
     * @return a {@link CommonTrustedCertificateSource} containing trusted certificates
     */
    public CommonTrustedCertificateSource createCommonTrustedCertificateSource(
        KeystoreProperties keystore) {
        var trustedCertSource = new CommonTrustedCertificateSource();
        trustedCertSource.importAsTrusted(createCertificateSource(keystore));

        return trustedCertSource;
    }

    /**
     * Creates a {@link CommonTrustedCertificateSource} using the provided truststore.
     *
     * <p>All certificates from the keystore are imported and marked as trusted.
     *
     * @param truststore the truststore configuration containing filename, content, password, and
     *                   type of the truststore
     *
     * @return a {@link CommonTrustedCertificateSource} containing all trusted certificates imported
     *     from the provided truststore
     */
    public CommonTrustedCertificateSource createCommonTrustedCertificateSource(
        ConnectorTruststore truststore) {
        var trustedCertSource = new CommonTrustedCertificateSource();
        trustedCertSource.importAsTrusted(createCertificateSource(truststore));

        return trustedCertSource;
    }
}
