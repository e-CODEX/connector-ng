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

import eu.ecodex.connector.infrastructure.property.dss.ConnectorDssProperties;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.source.TLSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Loader responsible for building DSS trusted lists certificate sources at startup.
 *
 * <p>For each configured trust source, a {@link TLValidationJob} is created and executed
 * to load trusted lists (TL) and lists of trusted lists (LOTL). The result of each job is a
 * {@link TrustedListsCertificateSource}, which is stored and made available for later use.
 *
 * <p>Loading process:
 * <ul>
 *     <li>Performs an offline refresh using cached data</li>
 *     <li>Attempts an online refresh to update the cache</li>
 *     <li>Falls back to cached data if the online refresh fails</li>
 * </ul>
 */
@Slf4j
@Component
public class ConnectorDssTrustedListLoader {
    private final ConnectorDssDataLoaderFactory dataLoaderFactory;
    private final ConnectorDssLotlFactory dssLotlFactory;
    private final ConnectorDssTrustedListFactory trustedListFactory;
    private final ConnectorDssProperties dssProperties;
    private Map<String, TrustedListsCertificateSource> trustedListsCertificateSourceMap
            = new HashMap<>();

    /**
     * Constructs and initializes the trusted list manager.
     *
     * <p>During construction, all configured trust sources are built and validated.
     *
     * @param dataLoaderFactory  factory providing online and offline data loaders
     * @param dssLotlFactory     factory for creating LOTL (List of Trusted Lists) sources
     * @param trustedListFactory factory for creating trusted list (TL) sources
     * @param dssProperties      DSS connector configuration properties
     */
    public ConnectorDssTrustedListLoader(
            ConnectorDssDataLoaderFactory dataLoaderFactory,
            ConnectorDssLotlFactory dssLotlFactory,
            ConnectorDssTrustedListFactory trustedListFactory,
            ConnectorDssProperties dssProperties) {
        this.dataLoaderFactory = dataLoaderFactory;
        this.dssLotlFactory = dssLotlFactory;
        this.trustedListFactory = trustedListFactory;
        this.dssProperties = dssProperties;
        initSource();
    }

    /**
     * Returns the {@link TrustedListsCertificateSource} associated with the given name.
     *
     * @param name the configured trust source name
     *
     * @return the corresponding certificate source, or {@code null} if not found
     */
    public TrustedListsCertificateSource getCertificateSource(String name) {
        return this.trustedListsCertificateSourceMap.get(name);
    }

    /**
     * Returns all configured trust source names.
     *
     * @return a set of trust source identifiers
     */
    public Set<String> getAllSourceNames() {
        return this.trustedListsCertificateSourceMap.keySet();
    }

    /**
     * Initializes all configured trust sources.
     *
     * <p>Each source is built and stored in an internal map, which is then made immutable.
     */
    private void initSource() {
        var trustSources = this.dssProperties.getTrustListSources();
        trustSources.forEach((key, value) -> buildTrustSource(key));
        this.trustedListsCertificateSourceMap = Collections.unmodifiableMap(
                trustedListsCertificateSourceMap
        );
    }

    private void buildTrustSource(String name) {
        var certificateSource = new TrustedListsCertificateSource();

        var job = new TLValidationJob();
        job.setDebug(log.isDebugEnabled());

        job.setListOfTrustedListSources(
                dssLotlFactory.getLotlSources().toArray(LOTLSource[]::new)
        );
        job.setTrustedListSources(
                trustedListFactory.getTrustedListSources().toArray(TLSource[]::new)
        );
        job.setOfflineDataLoader(dataLoaderFactory.createFileCacheDataLoader());
        job.setOnlineDataLoader(dataLoaderFactory.createOnlineDataLoader());
        job.setTrustedListCertificateSource(certificateSource);

        job.offlineRefresh();

        try {
            job.onlineRefresh();
        } catch (Exception e) {
            // Offline data still usable; log and continue rather than failing to startup
            log.warn(
                    "Online refresh failed for trust source [{}], falling back to cache: [{}]",
                    name, e.getMessage()
            );
        }

        log.info(
                "Configured trust source [{}] with {} trusted public keys",
                name, certificateSource.getNumberOfTrustedPublicKeys()
        );

        trustedListsCertificateSourceMap.put(name, certificateSource);
    }
}
