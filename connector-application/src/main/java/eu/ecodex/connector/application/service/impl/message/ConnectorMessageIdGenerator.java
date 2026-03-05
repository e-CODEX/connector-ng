/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message;

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Generates unique identifiers for connector messages.
 *
 * <p>This component is responsible for creating message identifiers used within the connector
 * system. The generated identifier follows a message-id style format consisting of a randomly
 * generated UUID and a fixed domain suffix.
 *
 * <p>Current format:</p>
 * <pre>
 *     &lt;uuid&gt;@eu.ecodex.connector
 * </pre>
 *
 * <p><strong>Note:</strong> The domain suffix is currently hardcoded.
 * Future implementations may derive it from message processing configuration or
 * environment-specific properties.
 */
@Component
public class ConnectorMessageIdGenerator {
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    public ConnectorMessageIdGenerator(
            ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    public String generateIdentifier() {
        var configuration = this.processingConfigurationProvider.getConfiguration();
        return String.format("%s@%s", UUID.randomUUID(), configuration.identifierSuffix());
    }
}
