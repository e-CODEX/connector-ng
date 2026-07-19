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

import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.springframework.stereotype.Component;

/**
 * Factory responsible for creating pre-configured DSS signature services.
 *
 * <p>This factory centralizes the creation of DSS services such as:
 * <ul>
 *     <li>{@link ASiCWithXAdESService}</li>
 *     <li>{@link PAdESService}</li>
 *     <li>{@link XAdESService}</li>
 * </ul>
 *
 * <p>Each service is initialized with:
 * <ul>
 *     <li>A certificate verifier (either provided or default)</li>
 *     <li>A composite TSP (Time Stamping Protocol) source</li>
 * </ul>
 *
 * <p><b>Note:</b> The {@link ASiCWithXAdESService} uses a configurable certificate
 * verifier, while the other services currently instantiate a default
 * {@link CommonCertificateVerifier}. This may be unified in future revisions.
 */
@Component
public class ConnectorDssServiceFactory {
    private final ConnectorDssCertificateVerifier certificateVerifier;
    private final ConnectorDssTimeStampingProtocol timeStampingProtocol;

    /**
     * Constructs a ConnectorDssServiceFactory with the specified certificate verifier and time
     * stamping protocol.
     *
     * @param certificateVerifier  the certificate verifier responsible for validating certificate
     *                             chains.
     * @param timeStampingProtocol the time stamping protocol used to provide time-stamp tokens for
     *                             signatures.
     */
    public ConnectorDssServiceFactory(
        ConnectorDssCertificateVerifier certificateVerifier,
        ConnectorDssTimeStampingProtocol timeStampingProtocol) {
        this.certificateVerifier = certificateVerifier;
        this.timeStampingProtocol = timeStampingProtocol;
    }

    /**
     * Creates an {@link ASiCWithXAdESService} configured with:
     * <ul>
     *     <li>A shared certificate verifier</li>
     *     <li>A composite TSP source</li>
     * </ul>.
     *
     * @return configured {@link ASiCWithXAdESService} instance
     *
     * @implNote A custom certificate verifier should be introduced to properly validate
     *     certificate chains instead of relying solely on the current configuration.
     */
    public ASiCWithXAdESService createAsicWithXAdESService() {
        var service = new ASiCWithXAdESService(
            // TODO use a custom verifier that checks the certificate chain
            certificateVerifier.getCommonCertificateVerifier()
        );
        service.setTspSource(timeStampingProtocol.getCompositeTspSource()); // ← here

        return service;
    }

    /**
     * Creates a {@link PAdESService} configured with:
     * <ul>
     *     <li>A default {@link CommonCertificateVerifier}</li>
     *     <li>A composite TSP source</li>
     * </ul>.
     *
     * @return configured {@link PAdESService} instance
     */
    public PAdESService createPadESSService() {
        var service = new PAdESService(new CommonCertificateVerifier(true));
        service.setTspSource(timeStampingProtocol.getCompositeTspSource());

        return service;
    }

    /**
     * Creates a {@link XAdESService} configured with:
     * <ul>
     *     <li>A default {@link CommonCertificateVerifier}</li>
     *     <li>A composite TSP source</li>
     * </ul>.
     *
     * @return configured {@link XAdESService} instance
     */
    public XAdESService createXadESSService() {
        var service = new XAdESService(new CommonCertificateVerifier(true));
        service.setTspSource(timeStampingProtocol.getCompositeTspSource()); // ← here

        return service;
    }
}
