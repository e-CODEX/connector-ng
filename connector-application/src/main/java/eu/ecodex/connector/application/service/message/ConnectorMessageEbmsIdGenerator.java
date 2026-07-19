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

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates unique ebMS message identifiers for connector messages.
 *
 * <p>The identifier is composed of two parts:
 * <ul>
 *   <li>A randomly generated {@link java.util.UUID}</li>
 *   <li>A configured ebMS identifier suffix</li>
 * </ul>
 *
 * <p>The suffix is retrieved from the {@link ConnectorMessageProcessingConfigurationProvider}
 * configuration and typically represents the domain or system identifier required by
 * the ebMS specification.
 */
@Slf4j
@Component
public class ConnectorMessageEbmsIdGenerator {
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    /**
     * Creates a new identifier generator.
     *
     * @param processingConfigurationProvider provider used to retrieve the message processing
     *                                        configuration, including the ebMS identifier suffix
     */
    public ConnectorMessageEbmsIdGenerator(
        ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    /**
     * Generates a new ebMS-compliant message identifier.
     *
     * <p>A random {@link java.util.UUID} is generated and combined with the configured ebMS
     * identifier suffix using the {@code '@'} separator.
     *
     * @return a unique message identifier in the format {@code <uuid>@<suffix>}
     */
    public String generateIdentifier() {
        log.debug("Generating new EBMS message identifier");
        var configuration = this.processingConfigurationProvider.getConfiguration();
        return String.format("%s@%s", UUID.randomUUID(), configuration.ebmsIdSuffix());
    }
}
