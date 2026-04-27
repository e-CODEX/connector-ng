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
import eu.ecodex.connector.infrastructure.property.dss.DssLotlSourceProperties;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ConnectorDssLotlConfig is a configuration class responsible for managing the configuration of
 * LOTL (List of Trusted Lists) sources in the application and converting these configurations into
 * {@link LOTLSource} objects.
 */
@Component
public class ConnectorDssLotlFactory {
    private final ConnectorDssCertificateSourceLoader certificateSourceLoader;
    private final ConnectorDssProperties dssProperties;

    /**
     * Constructs a new instance of the ConnectorDssLotlFactory class.
     *
     * @param certificateSourceLoader the certificate source loader used to create common trusted
     *                                certificate sources and load keystore configurations
     * @param dssProperties           the DSS properties containing trust list source
     *                                configurations
     */
    public ConnectorDssLotlFactory(
            ConnectorDssCertificateSourceLoader certificateSourceLoader,
            ConnectorDssProperties dssProperties) {
        this.certificateSourceLoader = certificateSourceLoader;
        this.dssProperties = dssProperties;
    }

    /**
     * Retrieves the list of LOTL (List of Trusted Lists) sources configured in the application. The
     * method processes trust list source groups and transforms each configured LOTL source property
     * into a corresponding LOTLSource object.
     *
     * @return a list of {@link LOTLSource} objects representing the configured LOTL sources.
     */
    public List<LOTLSource> getLotlSources() {
        var lotlSources = new ArrayList<LOTLSource>();
        dssProperties.getTrustListSources()
                     .values()
                     .forEach(group -> group
                             .getLotlSources()
                             .forEach(source -> {
                                          var lotlSource = createLotlSource(source);
                                          lotlSources.add(lotlSource);
                                      }
                             )
                     );

        return lotlSources;
    }

    private LOTLSource createLotlSource(DssLotlSourceProperties source) {
        var lotlSource = new LOTLSource();

        lotlSource.setUrl(source.getLotlUrl());
        lotlSource.setPivotSupport(source.isPivotSupport());

        if (StringUtils.hasText(source.getSigningCertificatesAnnouncementUri())) {
            lotlSource.setSigningCertificatesAnnouncementPredicate(
                    new OfficialJournalSchemeInformationURI(
                            source.getSigningCertificatesAnnouncementUri()
                    )
            );
        }

        var signingCerts = source.getSigningCerts();

        if (signingCerts != null) {
            var trustedCertSource = certificateSourceLoader.createCommonTrustedCertificateSource(
                    signingCerts);

            lotlSource.setCertificateSource(trustedCertSource);
        }

        return lotlSource;
    }
}
