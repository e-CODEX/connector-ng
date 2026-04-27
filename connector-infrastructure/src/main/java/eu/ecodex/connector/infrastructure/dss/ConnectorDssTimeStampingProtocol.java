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
import eu.europa.esig.dss.service.NonceSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.CompositeTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import java.util.LinkedHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Configures and builds a composite Time Stamping Protocol (TSP) source for use with DSS digital
 * signature services.
 *
 * <p>This class aggregates multiple Time Stamp Authority (TSA) endpoints
 * defined in {@link ConnectorDssProperties} into a {@link CompositeTSPSource}. The resulting
 * composite source can distribute requests (e.g. round-robin) and provide failover across multiple
 * TSAs.
 *
 * <p>Each TSA is configured with:
 * <ul>
 *   <li>A mandatory URL
 *   <li>An optional policy OID
 *   <li>A shared {@link NonceSource}
 *   <li>A shared {@link TimestampDataLoader} with proxy support</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class ConnectorDssTimeStampingProtocol {
    private final ConnectorDssProperties dssProperties;
    private final NonceSource nonceSource;
    private final ProxyConfig proxyConfig;

    @Getter
    private final CompositeTSPSource compositeTspSource;

    /**
     * Creates a new TSP configuration component.
     *
     * @param dssProperties configuration containing TSA definitions
     * @param nonceSource   source used to generate nonce for timestamp requests
     * @param proxyConfig   proxy configuration applied to TSA calls
     */
    public ConnectorDssTimeStampingProtocol(
            ConnectorDssProperties dssProperties,
            NonceSource nonceSource, ProxyConfig proxyConfig) {
        this.dssProperties = dssProperties;
        this.nonceSource = nonceSource;
        this.proxyConfig = proxyConfig;

        compositeTspSource = buildCompositeTspSource();
    }

    /**
     * Returns a CompositeTSPSource that round-robins across all configured TSAs. Keyed by name
     * (e.g. "t1", "ee-good-tsa") so you can add/remove in config only.
     */
    private CompositeTSPSource buildCompositeTspSource() {
        var timestampDataLoader = new TimestampDataLoader();
        timestampDataLoader.setProxyConfig(proxyConfig);
        var tspMap = new LinkedHashMap<String, TSPSource>();

        var servers = dssProperties.getTimeStampServers();

        // Validate URLs up front before mutating any state
        servers.forEach((name, tsaProps) -> {
            if (!StringUtils.hasText(tsaProps.getUrl())) {
                throw new IllegalArgumentException(
                        "TSA [" + name + "] is missing a required URL");
            }
        });

        servers.forEach((name, tsaProps) -> {
            log.info("registering TSA [{}] -> {}", name, tsaProps.getUrl());
            var source = new OnlineTSPSource();
            source.setNonceSource(nonceSource);
            source.setDataLoader(timestampDataLoader);
            source.setTspServer(tsaProps.getUrl());

            var policyID = tsaProps.getPolicyOid();

            if (StringUtils.hasText(policyID)) {
                source.setPolicyOid(policyID);
            }

            log.info(
                    "TSA [{}] -> {} with policy [{}] registered",
                    name,
                    tsaProps.getUrl(),
                    policyID
            );

            tspMap.put(name, source);
        });

        if (tspMap.isEmpty()) {
            log.warn("DSS: no TSA servers configured — timestamp levels (T/LT/LTA) will fail");
        }

        var composite = new CompositeTSPSource();
        composite.setTspSources(tspMap);

        return composite;
    }
}
