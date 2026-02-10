/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.initializer;

import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.property.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.domain.spi.property.ConnectorBusinessDomainPropertiesProvider;
import jakarta.annotation.Nonnull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes the default business domain for the connector system during application startup.
 *
 * <p>It ensures that at least one business domain exists in the system.
 * If no business domains are found, a default business domain is created and registered using
 * predefined properties.
 *
 * <p>The default business domain configuration is derived from an external provider
 * ({@link ConnectorBusinessDomainPropertiesProvider}). If no valid configuration is available, a
 * predefined constant ({@link ConnectorBusinessDomain#DEFAULT_BUSINESS_DOMAIN}) is used as the
 * fallback.
 */
@Component
public class ConnectorDefaultBusinessDomainInitializer implements ApplicationRunner {
    private final ConnectorBusinessDomainService businessDomainService;
    private final ConnectorBusinessDomainPropertiesProvider domainPropertiesProvider;

    public ConnectorDefaultBusinessDomainInitializer(
            ConnectorBusinessDomainService businessDomainService,
            ConnectorBusinessDomainPropertiesProvider domainPropertiesProvider) {
        this.businessDomainService = businessDomainService;
        this.domainPropertiesProvider = domainPropertiesProvider;
    }

    @Override
    public void run(@Nonnull ApplicationArguments args) {
        if (!businessDomainService.findAll().isEmpty()) {
            return;
        }

        var configProperties = domainPropertiesProvider.getProperties();
        var defaultDomain = createDefaultDomain(configProperties);

        businessDomainService.register(defaultDomain);
    }

    private ConnectorBusinessDomain createDefaultDomain(
            ConnectorBusinessDomainProperties configProperties) {
        if (configProperties == null) {
            return ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN;
        }

        return ConnectorBusinessDomain
                .builder()
                .identifier(
                        ConnectorBusinessDomainIdentifier
                                .builder()
                                .messageLaneIdentifier(
                                        configProperties.identifier())
                                .build()
                )
                .description(configProperties.description())
                .enabled(configProperties.enabled())
                .source(configProperties.source())
                .build();
    }
}
