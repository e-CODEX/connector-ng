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

import eu.ecodex.connector.infrastructure.property.certificate.ConnectorCertificateVerifierProperties;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
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
     * @throws IllegalArgumentException if required, truststore configuration is missing
     */
    public CommonCertificateVerifier createCommonCertificateVerifier(
            ConnectorCertificateVerifierProperties properties) {
        log.debug("Initializing certificate verifier");
        var commonCertificateVerifier = new CommonCertificateVerifier(true);

        configureAia(properties, commonCertificateVerifier);
        configureOcsp(properties, commonCertificateVerifier);
        configureCrl(properties, commonCertificateVerifier);

        var trustedCertificateSourcesList = new ListCertificateSource();
        var trustedListSourceName = properties.getTrustedListSource();

        if (StringUtils.hasText(trustedListSourceName)) {
            var certificateSource = trustedListLoader.getCertificateSource(trustedListSourceName);
            if (certificateSource != null) {
                trustedCertificateSourcesList.add(certificateSource);
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

        if (properties.isTruststoreEnabled()) {
            var truststore = properties.getTruststore();
            if (truststore == null) {
                throw new IllegalArgumentException(
                        "Trust store is set to enabled, but it is not configured!");
            }
            var certificateSourceFromStore =
                    certificateSourceLoader.createCommonTrustedCertificateSource(truststore);
            trustedCertificateSourcesList.add(certificateSourceFromStore);
        } else {
            log.debug("truststore is not enabled");
        }

        if (trustedCertificateSourcesList.isEmpty()) {
            log.warn("no trusted certificate source has been configured");
        } else {
            commonCertificateVerifier.setTrustedCertSources(trustedCertificateSourcesList);
            log.debug(
                    "Setting trusted certificate sources: [{}]",
                    trustedCertificateSourcesList
                            .getSources()
                            .stream()
                            .map(s -> "[" + s.getCertificateSourceType()
                                      + " entries " + s.getCertificates().size() + "]")
                            .collect(Collectors.joining(","))
            );
        }

        if (properties.isIgnoreStoreEnabled()) {
            var ignoreStore = properties.getIgnoreStore();
            if (ignoreStore == null) {
                throw new IllegalArgumentException(
                        "Trust store is set to enabled, but it is not configured!");
            }

            var certificateSourceFromStore =
                    certificateSourceLoader.createCommonTrustedCertificateSource(ignoreStore);
            commonCertificateVerifier.setAdjunctCertSources(certificateSourceFromStore);
            log.debug(
                    "Setting untrusted certificate source: [{}]", certificateSourceFromStore);
        } else {
            log.debug("ignore store is not enabled");
        }

        return commonCertificateVerifier;
    }

    private void configureAia(
            ConnectorCertificateVerifierProperties properties,
            CommonCertificateVerifier commonCertificateVerifier) {
        if (properties.isAiaEnabled()) {
            log.info("AIA loading is enabled");
            var aiaSource = new DefaultAIASource(dataLoader);
            commonCertificateVerifier.setAIASource(aiaSource);
        } else {
            log.info("AIA loading is disabled");
        }
    }

    private void configureOcsp(
            ConnectorCertificateVerifierProperties properties,
            CommonCertificateVerifier commonCertificateVerifier) {
        if (properties.isOcspEnabled()) {
            log.info("OCSP loading is enabled");
            commonCertificateVerifier.setOcspSource(onlineOCSPSource);
        } else {
            log.info("OCSP loading is disabled");
        }
    }

    private void configureCrl(
            ConnectorCertificateVerifierProperties properties,
            CommonCertificateVerifier commonCertificateVerifier) {
        if (properties.isCrlEnabled()) {
            log.info("CRL loading is enabled");
            commonCertificateVerifier.setCrlSource(onlineCRLSource);
        } else {
            log.info("CRL loading is disabled");
        }
    }
}
