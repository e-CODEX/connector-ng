/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.soap;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class BackendServiceTest extends AbstractIntegrationTest {
    protected DomibusConnectorBackendWebService createClient(int port) {
        var address = "http://localhost:" + port + "/services/backend";

        var factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DomibusConnectorBackendWebService.class);
        factory.setAddress(address);
        factory.setWsdlURL("classpath:wsdl/v1/DomibusConnectorBackendWebService.wsdl");

        return (DomibusConnectorBackendWebService) factory.create();
    }
}
