/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web;

import eu.ecodex.connector.application.service.usecase.link.ConnectorFindLinkPartner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Service responsible for resolving the backend client identifier based on a certificate
 * distinguished name (DN).
 *
 * <p>Typically used in authentication or request verification flows where the client identity is
 * derived from an X.509 certificate.
 */
@Slf4j
@Component
public class ConnectorBackendClientVerifier {
    private final ConnectorFindLinkPartner findLinkPartnerService;

    /**
     * Creates a new verifier instance.
     *
     * @param findLinkPartnerService service used to retrieve connector link partners based on
     *                               certificate information
     */
    public ConnectorBackendClientVerifier(ConnectorFindLinkPartner findLinkPartnerService) {
        this.findLinkPartnerService = findLinkPartnerService;
    }

    /**
     * Resolves the backend client name associated with the given certificate distinguished name.
     *
     * @param certificateDn the distinguished name extracted from the client certificate
     *
     * @return the backend client identifier corresponding to the matched connector link partner
     */
    public String getBackendClient(@NonNull String certificateDn) {
        log.debug("retrieving backend client: {}", certificateDn);

        // TODO to be completed, certificateDn should be retrieved from the certificate

        var linkPartner = this.findLinkPartnerService.findByCertificateDn(certificateDn);

        return linkPartner.name().name();
    }
}
