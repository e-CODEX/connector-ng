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

import eu.ecodex.connector.infrastructure.property.link.ConnectorLinkProperties;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.WebServiceContext;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class BackendSoapWebServiceConfig {
    private final BackendWebServiceFactory backendWebServiceFactory;
    private final ConnectorLinkProperties connectorLinkProperties;

    public BackendSoapWebServiceConfig(
            BackendWebServiceFactory backendWebServiceFactory,
            ConnectorLinkProperties connectorLinkProperties) {
        this.backendWebServiceFactory = backendWebServiceFactory;
        this.connectorLinkProperties = connectorLinkProperties;
    }

    @Bean
    public List<Endpoint> backendWebServiceEndpoints() {
        return connectorLinkProperties.getBackend()
                                      .stream()
                                      .map(backendWebServiceFactory::createEndpoint)
                                      .toList();
    }

    @Bean
    public WebServiceContext webServiceContext() {
        return new WebServiceContextImpl();
    }
}
