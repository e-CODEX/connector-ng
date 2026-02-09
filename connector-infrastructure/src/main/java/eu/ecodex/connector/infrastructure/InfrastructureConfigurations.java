/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure;

import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.api.link.ConnectorLinkSubmissionService;
import eu.ecodex.connector.domain.service.link.ConnectorLinkSubmissionServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * InfrastructureConfigurations class for configuring the infrastructure-specific components.
 */
@Configuration
@ConfigurationPropertiesScan
@EnableJpaRepositories(
        basePackages = {"eu.ecodex.connector.infrastructure.database.repository"}
)
public class InfrastructureConfigurations {
    @Bean
    ConnectorLinkSubmissionService connectorLinkSubmissionService(
            @Qualifier("connectorBackendLinkEventPublisher")
            ConnectorEventPublisher backendLinkEventPublisher,
            @Qualifier("connectorGatewayLinkEventPublisher")
            ConnectorEventPublisher gatewayLinkEventPublisher
    ) {
        return new ConnectorLinkSubmissionServiceImpl(
                backendLinkEventPublisher, gatewayLinkEventPublisher
        );
    }
}
