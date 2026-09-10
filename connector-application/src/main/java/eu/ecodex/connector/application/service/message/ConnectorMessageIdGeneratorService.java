/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorMessageIdGenerator}.
 */
@Slf4j
@Service
public class ConnectorMessageIdGeneratorService implements ConnectorMessageIdGenerator {
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    public ConnectorMessageIdGeneratorService(
        ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    @Override
    public String execute() {
        log.debug("Generating new message identifier");
        var configuration = this.processingConfigurationProvider.getConfiguration();
        return String.format("%s@%s", UUID.randomUUID(), configuration.identifierSuffix());
    }
}
