/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.initializer;

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.application.service.usecase.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.infrastructure.property.businessdomain.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.DefaultBusinessDomainProperties;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Initializes the connector's business domains based on provided configuration and existing data.
 */
@Slf4j
@Component
public class ConnectorBusinessDomainInitializer implements ApplicationRunner {
    private static final String FILE_PREFIX = "file:";
    private final ConnectorRegisterBusinessDomain registerBusinessDomainService;
    private final ConnectorListBusinessDomain listBusinessDomainService;
    private final ConnectorRegisterProcessingMode registerProcessingModeService;
    private final ConnectorBusinessDomainProperties domainProperties;

    /**
     * Initializes the connector's business domains based on provided configuration and existing
     * data. This constructor sets up the necessary services and properties required for managing
     * business domains in the connector environment.
     *
     * @param registerBusinessDomainService the service responsible for registering new business
     *                                      domains into the system.
     * @param listBusinessDomainService     the service responsible for listing all existing
     *                                      business domains in the system.
     * @param registerProcessingModeService the service responsible for registering new processing
     *                                      modes
     * @param domainProperties              the configuration properties containing default business
     *                                      domain information.
     */
    public ConnectorBusinessDomainInitializer(
            ConnectorRegisterBusinessDomain registerBusinessDomainService,
            ConnectorListBusinessDomain listBusinessDomainService,
            ConnectorRegisterProcessingMode registerProcessingModeService,
            ConnectorBusinessDomainProperties domainProperties) {
        this.registerBusinessDomainService = registerBusinessDomainService;
        this.listBusinessDomainService = listBusinessDomainService;
        this.registerProcessingModeService = registerProcessingModeService;
        this.domainProperties = domainProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing business domains");

        var defaultBusinessDomains = domainProperties.getDefaults();
        if (defaultBusinessDomains != null && !defaultBusinessDomains.isEmpty()) {
            registerDefaultBusinessDomains(defaultBusinessDomains);
        } else if (listBusinessDomainService.execute().isEmpty()) {
            log.info("No default business domains configured and none registered yet");
            registerBusinessDomainService.execute(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN);
        } else {
            log.debug(
                    "No default business domains configured; existing domains found, nothing to do"
            );
        }
    }

    private void registerDefaultBusinessDomains(
            List<DefaultBusinessDomainProperties> defaultBusinessDomains) {
        log.info("Found {} default business domains", defaultBusinessDomains.size());

        for (var properties : defaultBusinessDomains) {
            var businessDomain = toBusinessDomain(properties);
            try {
                registerBusinessDomainService.execute(businessDomain);
            } catch (Exception e) {
                log.warn(
                        "Could not register business domain [{}]: Reason: [{}]",
                        properties.getIdentifier(), e.getMessage()
                );
            }

            if (properties.getPmodeFile() != null) {
                log.info("Found p-mode file for domain [{}]", properties.getIdentifier());

                try {
                    var processingMode = toProcessingMode(properties);
                    registerProcessingModeService.execute(
                            businessDomain.identifier(), processingMode
                    );
                } catch (Exception e) {
                    log.warn(
                            "Could not register configured p-mode for domain [{}]: Reason: [{}]",
                            properties.getIdentifier(), e.getMessage()
                    );
                }
            }
        }
    }

    private ConnectorBusinessDomain toBusinessDomain(DefaultBusinessDomainProperties properties) {
        return ConnectorBusinessDomain
                .builder()
                .identifier(
                        ConnectorBusinessDomainIdentifier
                                .builder()
                                .messageLaneIdentifier(properties.getIdentifier())
                                .build())
                .description(properties.getDescription())
                .enabled(properties.isEnabled())
                .source(ConnectorConfigurationSource.IMPLEMENTATION)
                .build();
    }

    private ConnectorProcessingMode toProcessingMode(DefaultBusinessDomainProperties properties)
            throws IOException {
        return ConnectorProcessingMode.builder()
                                      .description(String.format(
                                              "Default processing mode for %s",
                                              properties.getIdentifier()
                                      ))
                                      .filename(properties.getPmodeFile())
                                      .content(getPmodeFile(properties.getPmodeFile()))
                                      .build();
    }

    private String getPmodeFile(String path) throws IOException {
        if (path.startsWith(FILE_PREFIX)) {
            var resourcePath = path.substring(FILE_PREFIX.length());

            return Files.readString(Path.of(resourcePath));
        }

        try (var stream = URI.create(path).toURL().openStream()) {
            return stream.toString();
        } catch (Exception e) {
            return Files.readString(Path.of(path));
        }
    }
}
