/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.soap;

import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWSService;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorMerlinPropertiesFactory;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorWsPolicyLoader;
import eu.ecodex.connector.infrastructure.property.link.BackendLinkProperties;
import jakarta.xml.ws.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.rt.security.SecurityConstants;
import org.springframework.stereotype.Component;

/**
 * Factory class responsible for creating and configuring backend web service endpoints. This class
 * sets up the necessary configurations, such as security policies, address bindings, WSDL location,
 * and MTOM support, for exposing the backend web service. It uses the provided {@code Bus},
 * {@code ConnectorMerlinPropertiesFactory}, and {@code DomibusConnectorBackendWebService} to create
 * a fully functional CXF {@code Endpoint} instance.
 */
@Slf4j
@Component
public class BackendWebServiceFactory {
    private final Bus bus;
    private final ConnectorMerlinPropertiesFactory merlinPropertiesFactory;
    private final DomibusConnectorBackendWebService backendWebService;

    /**
     * Constructs a {@code BackendWebServiceFactory} instance responsible for creating and managing
     * the backend web service within the connector's SOAP-based infrastructure.
     *
     * @param bus                     the Apache CXF {@code Bus} used for configuring and managing
     *                                the runtime environment of web services. Must not be null.
     * @param merlinPropertiesFactory the factory for creating cryptographic properties required by
     *                                WSS4J's Merlin Crypto implementation. Must not be null.
     * @param backendWebService       the web service implementation responsible for handling
     *                                backend requests. Must not be null.
     */
    public BackendWebServiceFactory(
        Bus bus,
        ConnectorMerlinPropertiesFactory merlinPropertiesFactory,
        DomibusConnectorBackendWebService backendWebService) {
        this.bus = bus;
        this.backendWebService = backendWebService;
        this.merlinPropertiesFactory = merlinPropertiesFactory;
    }

    Endpoint createEndpoint(BackendLinkProperties linkProperties) {
        var linkConfigProperties = linkProperties.getLinkConfig().getProperties();

        var endpoint = new EndpointImpl(bus, backendWebService);

        endpoint.setAddress(linkConfigProperties.getPublishAddress());
        endpoint.setServiceName(DomibusConnectorBackendWSService.SERVICE);
        endpoint.setEndpointName(
            DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
        endpoint.setWsdlLocation("classpath:wsdl/v1/DomibusConnectorBackendWebService.wsdl");

        // Common properties
        endpoint.getProperties().put("security.store.bytes.in.attachment", true);
        endpoint.getProperties().put("security.enable.streaming", true);
        endpoint.getProperties().put("mtom-enabled", true);

        endpoint.getProperties().put(
            SecurityConstants.SIGNATURE_PROPERTIES,
            merlinPropertiesFactory.createSigningProperties(linkConfigProperties.getEndpoint())
        );
        endpoint.getProperties().put(SecurityConstants.ENCRYPT_USERNAME, "useReqSigCert");
        endpoint.getProperties().put(
            SecurityConstants.ENCRYPT_PROPERTIES,
            merlinPropertiesFactory.createEncryptionProperties(
                linkConfigProperties.getEndpoint())
        );
        endpoint.getProperties().put(
            SecurityConstants.CALLBACK_HANDLER,
            new DefaultWsCallbackHandler()
        );

        if (linkConfigProperties.isLoggingEnabled()) {
            endpoint.getFeatures().add(new LoggingFeature());
        }

        // apply ws policy
        var policyLoader = new ConnectorWsPolicyLoader(
            linkConfigProperties.getWsPolicy()
        );

        endpoint.getFeatures().add(policyLoader.loadPolicyFeature());

        endpoint.publish();

        log.debug(
            "Published Backend WebService [{}] under [{}]",
            DomibusConnectorBackendWSService.class, linkConfigProperties.getPublishAddress()
        );

        return endpoint;
    }
}
