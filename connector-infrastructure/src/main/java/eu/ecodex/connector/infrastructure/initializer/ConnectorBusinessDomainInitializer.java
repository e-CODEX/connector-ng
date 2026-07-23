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

import eu.ecodex.connector.application.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.application.port.api.pmode.ConnectorRegisterProcessingMode;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.domain.model.security.KeystoreType;
import eu.ecodex.connector.infrastructure.property.businessdomain.ConnectorBusinessDomainProperties;
import eu.ecodex.connector.infrastructure.property.businessdomain.DefaultBusinessDomainProperties;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Initializes the connector's business domains based on provided configuration and existing data.
 */
@Slf4j
@Component
public class ConnectorBusinessDomainInitializer implements ApplicationRunner {
    private final ConnectorRegisterBusinessDomain registerBusinessDomainService;
    private final ConnectorListBusinessDomain listBusinessDomainService;
    private final ConnectorRegisterProcessingMode registerProcessingModeService;
    private final ConnectorBusinessDomainProperties domainProperties;
    private final ResourceLoader resourceLoader;

    /**
     * Creates the initializer.
     *
     * @param registerBusinessDomainService the service registering new business domains.
     * @param listBusinessDomainService     the service listing existing business domains.
     * @param registerProcessingModeService the service registering new processing modes.
     * @param domainProperties              the configured default business domains.
     * @param resourceLoader                resolves {@code file:}, {@code classpath:} and
     *                                      {@code http(s):} locations uniformly.
     */
    public ConnectorBusinessDomainInitializer(
        ConnectorRegisterBusinessDomain registerBusinessDomainService,
        ConnectorListBusinessDomain listBusinessDomainService,
        ConnectorRegisterProcessingMode registerProcessingModeService,
        ConnectorBusinessDomainProperties domainProperties,
        ResourceLoader resourceLoader) {
        this.registerBusinessDomainService = registerBusinessDomainService;
        this.listBusinessDomainService = listBusinessDomainService;
        this.registerProcessingModeService = registerProcessingModeService;
        this.domainProperties = domainProperties;
        this.resourceLoader = resourceLoader;
    }

    private static ConnectorBusinessDomainIdentifier businessDomainIdentifier(String identifier) {
        return ConnectorBusinessDomainIdentifier
            .builder()
            .messageLaneIdentifier(identifier)
            .build();
    }

    static String filenameOf(String location) {
        var withoutQuery = location.split("[?#]", 2)[0];
        var lastSeparator = Math.max(withoutQuery.lastIndexOf('/'), withoutQuery.lastIndexOf('\\'));

        return lastSeparator < 0 ? withoutQuery : withoutQuery.substring(lastSeparator + 1);
    }

    @Override
    public void run(ApplicationArguments args) {
        var defaults = domainProperties.getDefaults();

        if (defaults == null || defaults.isEmpty()) {
            registerFallbackBusinessDomain();
            return;
        }

        log.info("Initializing {} configured business domain(s)", defaults.size());
        defaults.forEach(this::initializeBusinessDomain);
    }

    private void registerFallbackBusinessDomain() {
        if (!listBusinessDomainService.execute().isEmpty()) {
            log.debug("No default business domains configured; existing domains found");
            return;
        }

        log.info("No default business domains configured and none registered yet");
        registerBusinessDomainService.execute(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN);
    }

    private void initializeBusinessDomain(DefaultBusinessDomainProperties properties) {
        var identifier = properties.getIdentifier();

        try {
            registerBusinessDomainService.execute(toBusinessDomain(properties));
            log.info("Business domain [{}] registered", identifier);
        } catch (ConnectorBusinessDomainAlreadyExistsException e) {
            log.debug("Business domain [{}] already registered", identifier);
        }

        registerConfiguredProcessingMode(properties);
    }

    private void registerConfiguredProcessingMode(DefaultBusinessDomainProperties properties) {
        var identifier = properties.getIdentifier();
        var pmode = properties.getPmode();

        if (pmode == null || !StringUtils.hasText(pmode.getFile())) {
            log.debug("No processing mode configured for business domain [{}]", identifier);
            return;
        }

        if (!StringUtils.hasText(pmode.getTruststore())) {
            throw new IllegalStateException(
                "Business domain [%s] declares a processing mode file but no truststore"
                    .formatted(identifier));
        }

        try {
            registerProcessingModeService.execute(
                businessDomainIdentifier(identifier), toProcessingMode(properties));
            log.info("Processing mode registered for business domain [{}]", identifier);
        } catch (ConnectorProcessingModeException e) {
            log.debug("Business domain [{}] already has a processing mode", identifier);
        } catch (IOException e) {
            // Misconfigured location: fail fast rather than start in a half-configured state.
            throw new IllegalStateException(
                "Could not read the processing mode configured for business domain [%s]"
                    .formatted(identifier), e
            );
        }
    }

    private ConnectorBusinessDomain toBusinessDomain(DefaultBusinessDomainProperties properties) {
        return ConnectorBusinessDomain
            .builder()
            .identifier(businessDomainIdentifier(properties.getIdentifier()))
            .description(properties.getDescription())
            .enabled(properties.isEnabled())
            .source(ConnectorConfigurationSource.IMPLEMENTATION)
            .build();
    }

    private ConnectorProcessingMode toProcessingMode(DefaultBusinessDomainProperties properties)
        throws IOException {
        var pmode = properties.getPmode();

        var truststoreLocation = pmode.getTruststore();
        var truststoreFilename = filenameOf(truststoreLocation);

        var truststore = ConnectorTruststore
            .builder()
            .filename(truststoreFilename)
            .password(pmode.getTruststorePassword())
            .content(read(truststoreLocation, "truststore"))
            .type(KeystoreType.fromFileName(truststoreFilename)
                              .orElseThrow(() -> new IllegalStateException(
                                  "Cannot determine the keystore type of [%s]"
                                      .formatted(truststoreFilename))))
            .build();

        return ConnectorProcessingMode
            .builder()
            .description("Default processing mode for %s".formatted(properties.getIdentifier()))
            .filename(filenameOf(pmode.getFile()))
            .content(new String(read(pmode.getFile(), "processing mode file")))
            .truststore(truststore)
            .build();
    }

    private byte[] read(String location, String description) throws IOException {
        var resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new FileNotFoundException(
                "No %s found at [%s]".formatted(description, location));
        }

        try (var stream = resource.getInputStream()) {
            return stream.readAllBytes();
        }
    }
}
