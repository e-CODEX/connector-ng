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
import eu.ecodex.connector.infrastructure.property.certificate.ConnectorCertificateVerifierProperties;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Factory and configurator for {@link CommonCertificateVerifier} instances.
 *
 * <p>This class builds certificate verifiers based on the provided configuration, combining:
 * <ul>
 *     <li>Trusted lists loaded via {@link ConnectorDssTrustedListLoader}
 *     <li>Optional truststore and ignore store
 *     <li>Online validation sources (AIA, OCSP, CRL)
 * </ul>
 *
 * <p>Each invocation of
 * {@link #createCommonCertificateVerifier(ConnectorCertificateVerifierProperties)}
 * produces a newly configured verifier instance.
 */
@Slf4j
@Component
public class ConnectorDssCertificateVerifier {
    private final ConnectorDssTrustedListLoader trustedListLoader;
    private final ConnectorDssCertificateSourceLoader certificateSourceLoader;
    private final OnlineCRLSource onlineCRLSource;
    private final OnlineOCSPSource onlineOCSPSource;
    private final DataLoader dataLoader;

    @Getter
    private final CommonCertificateVerifier commonCertificateVerifier;

    /**
     * Constructs a new verifier factory with required dependencies.
     *
     * @param certificateSourceLoader loader for creating certificate sources from keystores
     * @param trustedListLoader       loader providing trusted list certificate sources
     * @param onlineCRLSource         CRL source used for revocation checking
     * @param onlineOCSPSource        OCSP source used for revocation checking
     * @param dataLoader              data loader used for AIA resolution
     */
    public ConnectorDssCertificateVerifier(
        ConnectorDssCertificateSourceLoader certificateSourceLoader,
        ConnectorDssTrustedListLoader trustedListLoader,
        OnlineCRLSource onlineCRLSource,
        OnlineOCSPSource onlineOCSPSource,
        DataLoader dataLoader) {
        this.certificateSourceLoader = certificateSourceLoader;
        this.trustedListLoader = trustedListLoader;
        this.onlineCRLSource = onlineCRLSource;
        this.onlineOCSPSource = onlineOCSPSource;
        this.dataLoader = dataLoader;

        commonCertificateVerifier = new CommonCertificateVerifier(true);
    }


    /**
     * Creates and configures a {@link CommonCertificateVerifier} based on the provided properties.
     *
     * <p>Configuration includes:
     * <ul>
     *     <li>Enabling/disabling AIA, OCSP, and CRL sources
     *     <li>Adding trusted certificate sources from trusted lists
     *     <li>Optionally adding certificates from a truststore
     *     <li>Optionally configuring an "ignore" (adjunct) certificate source
     * </ul>
     *
     * @param properties the verifier configuration properties
     *
     * @return a fully configured {@link CommonCertificateVerifier}
     *
     * @throws IllegalArgumentException if required, truststore configuration is missing
     */
    public CommonCertificateVerifier createCommonCertificateVerifier(
        ConnectorCertificateVerifierProperties properties) {
        var configuredTrusted = resolveConfiguredTruststore(properties);
        return build(properties, configuredTrusted);
    }

    /**
     * Creates a {@link CommonCertificateVerifier} instance, configured using the provided verifier
     * properties and truststore.
     *
     * <p>Configuration includes:
     * <ul>
     *     <li>Enabling/disabling AIA, OCSP, and CRL sources
     *     <li>Adding trusted certificate sources from trusted lists
     *     <li>Optionally adding certificates from a truststore
     *     <li>Optionally configuring an "ignore" (adjunct) certificate source
     * </ul>
     *
     * @param properties the configuration properties for the certificate verifier, including
     *                   options such as enabling OCSP, CRL, AIA, and truststore usage
     * @param truststore the truststore containing trusted certificates, which will be imported and
     *                   used by the certificate verifier; must not be null
     *
     * @return a fully configured {@link CommonCertificateVerifier} instance
     *
     * @throws IllegalArgumentException if the provided truststore is null
     */
    public CommonCertificateVerifier createCommonCertificateVerifier(
        ConnectorCertificateVerifierProperties properties, ConnectorTruststore truststore) {

        if (truststore == null) {
            throw new IllegalArgumentException("Message truststore must not be null");
        }

        var messageTrusted =
            certificateSourceLoader.createCommonTrustedCertificateSource(truststore);
        return build(properties, messageTrusted);
    }


    private CommonCertificateVerifier build(
        ConnectorCertificateVerifierProperties properties,
        CommonTrustedCertificateSource trustedStore) {
        log.debug("Initializing certificate verifier");
        var verifier = new CommonCertificateVerifier(true);

        configureAia(properties, verifier);
        configureOcsp(properties, verifier);
        configureCrl(properties, verifier);

        var trustedSources = new ListCertificateSource();
        addTrustedListSource(properties, trustedSources);

        if (trustedStore != null) {
            trustedSources.add(trustedStore);
        } else {
            log.debug("no trusted store provided for this verifier");
        }

        applyTrustedSources(verifier, trustedSources);
        configureIgnoreStore(properties, verifier);

        return verifier;
    }


    private CommonTrustedCertificateSource resolveConfiguredTruststore(
        ConnectorCertificateVerifierProperties properties) {
        if (!properties.isTruststoreEnabled()) {
            log.debug("truststore is not enabled");
            return null;
        }

        var truststore = properties.getTruststore();

        if (truststore == null) {
            throw new IllegalArgumentException(
                "Trust store is set to enabled, but it is not configured!");
        }

        return certificateSourceLoader.createCommonTrustedCertificateSource(truststore);
    }


    private void addTrustedListSource(
        ConnectorCertificateVerifierProperties properties,
        ListCertificateSource trustedSources) {
        var trustedListSourceName = properties.getTrustedListSource();

        if (!StringUtils.hasText(trustedListSourceName)) {
            return;
        }

        var certificateSource = trustedListLoader.getCertificateSource(trustedListSourceName);

        if (certificateSource != null) {
            trustedSources.add(certificateSource);
        } else {
            log.warn(
                "There is no TrustedListsCertificateSource with key [{}] configured.",
                trustedListSourceName
            );
            log.warn(
                "Available TrustedListsCertificateSource are [{}]",
                trustedListLoader.getAllSourceNames()
            );
        }
    }

    private void applyTrustedSources(
        CommonCertificateVerifier verifier,
        ListCertificateSource trustedSources) {
        if (trustedSources.isEmpty()) {
            log.warn("no trusted certificate source has been configured");
            return;
        }
        verifier.setTrustedCertSources(trustedSources);
        log.debug(
            "Setting trusted certificate sources: [{}]",
            trustedSources.getSources()
                          .stream()
                          .map(s -> "[" + s.getCertificateSourceType()
                              + " entries " + s.getCertificates().size() + "]")
                          .collect(Collectors.joining(","))
        );
    }

    private void configureIgnoreStore(
        ConnectorCertificateVerifierProperties properties,
        CommonCertificateVerifier verifier) {
        if (!properties.isIgnoreStoreEnabled()) {
            log.debug("ignore store is not enabled");
            return;
        }

        var ignoreStore = properties.getIgnoreStore();
        if (ignoreStore == null) {
            throw new IllegalArgumentException(
                "Ignore store is set to enabled, but it is not configured!");
        }

        var ignoreSource = certificateSourceLoader.createCommonTrustedCertificateSource(
            ignoreStore
        );
        verifier.setAdjunctCertSources(ignoreSource);
        log.debug("Setting untrusted certificate source: [{}]", ignoreSource);
    }

    private void configureAia(
        ConnectorCertificateVerifierProperties properties,
        CommonCertificateVerifier verifier) {
        if (properties.isAiaEnabled()) {
            log.info("AIA loading is enabled");
            verifier.setAIASource(new DefaultAIASource(dataLoader));
        } else {
            log.info("AIA loading is disabled");
        }
    }

    private void configureOcsp(
        ConnectorCertificateVerifierProperties properties,
        CommonCertificateVerifier verifier) {
        if (properties.isOcspEnabled()) {
            log.info("OCSP loading is enabled");
            verifier.setOcspSource(onlineOCSPSource);
        } else {
            log.info("OCSP loading is disabled");
        }
    }

    private void configureCrl(
        ConnectorCertificateVerifierProperties properties,
        CommonCertificateVerifier verifier) {
        if (properties.isCrlEnabled()) {
            log.info("CRL loading is enabled");
            verifier.setCrlSource(onlineCRLSource);
        } else {
            log.info("CRL loading is disabled");
        }
    }
}
