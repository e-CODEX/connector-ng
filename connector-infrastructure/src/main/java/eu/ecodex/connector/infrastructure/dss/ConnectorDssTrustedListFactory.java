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
import eu.ecodex.connector.infrastructure.property.dss.DssTlSourceProperties;
import eu.europa.esig.dss.tsl.source.TLSource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Factory responsible for creating {@link TLSource} instances based on configured trusted list
 * properties.
 *
 * <p>It builds trusted list sources using the provided {@link ConnectorDssProperties}
 * and enriches them with certificate sources when signing certificates are defined.
 */
@Component
public class ConnectorDssTrustedListFactory {
    private final ConnectorDssProperties dssProperties;
    private final ConnectorDssCertificateSourceLoader certificateSourceLoader;

    /**
     * Constructs a new factory with the given configuration.
     *
     * @param dssProperties           the DSS connector properties containing trusted list
     *                                definitions
     * @param certificateSourceLoader the certificate source used to build trusted certificate
     *                                sources
     */
    public ConnectorDssTrustedListFactory(
            ConnectorDssProperties dssProperties,
            ConnectorDssCertificateSourceLoader certificateSourceLoader) {
        this.dssProperties = dssProperties;
        this.certificateSourceLoader = certificateSourceLoader;
    }

    private TLSource createTrustedListSource(DssTlSourceProperties properties) {
        var source = new TLSource();
        source.setUrl(properties.getTlUrl());
        var signingCerts = properties.getSigningCerts();

        if (signingCerts != null) {
            var trustedCertSource = certificateSourceLoader.createCommonTrustedCertificateSource(
                    signingCerts);

            source.setCertificateSource(trustedCertSource);
        }

        return source;
    }

    /**
     * Builds all configured {@link TLSource} instances.
     *
     * <p>Iterates through all trusted list groups defined in the properties and
     * aggregates their sources into a single list.
     *
     * @return a list of configured {@link TLSource} objects
     */
    public List<TLSource> getTrustedListSources() {
        var tlSources = new ArrayList<TLSource>();
        dssProperties.getTrustListSources()
                     .values()
                     .forEach(group -> group
                             .getTlSources()
                             .forEach(source -> {
                                          var trustedListSource = createTrustedListSource(source);
                                          tlSources.add(trustedListSource);
                                      }
                             )
                     );

        return tlSources;
    }
}
