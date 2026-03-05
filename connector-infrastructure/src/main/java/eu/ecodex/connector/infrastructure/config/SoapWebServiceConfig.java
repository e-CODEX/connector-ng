/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWSService;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.WebServiceContext;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for SOAP web service endpoints.
 *
 * <p>This configuration registers and publishes the SOAP backend web service using Apache CXF.
 * It configures the service address, WSDL location, service name, endpoint name, and enables MTOM
 * support for efficient binary data transmission.
 */
@Configuration
public class SoapWebServiceConfig {
    private final Bus bus;
    private final DomibusConnectorBackendWebService backendWebService;

    public SoapWebServiceConfig(Bus bus, DomibusConnectorBackendWebService backendWebService) {
        this.bus = bus;
        this.backendWebService = backendWebService;
    }

    @Bean
    Endpoint backendWebServiceEndpoint() {
        var endpoint = new EndpointImpl(bus, backendWebService);

        endpoint.setAddress("/backend");
        endpoint.setServiceName(DomibusConnectorBackendWSService.SERVICE);
        endpoint.setEndpointName(
                DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
        endpoint.setWsdlLocation("classpath:wsdl/v1/DomibusConnectorBackendWebService.wsdl");
        endpoint.publish();

        endpoint.getProperties().put("mtom-enabled", true);

        return endpoint;
    }

    @Bean
    WebServiceContext webServiceContext() {
        return new WebServiceContextImpl();
    }
}
