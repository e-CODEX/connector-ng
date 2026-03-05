/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom; Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL; Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.property;

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.model.ProcessingModeVerificationMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the connector message processing.
 */
@Getter
@Setter
@Configuration
@SuppressWarnings("checkstyle:LineLength")
@ConfigurationProperties(prefix = "connector.message-processing")
public class ConnectorMessageProcessingProperties implements
        ConnectorMessageProcessingConfigurationProvider {
    private final boolean ebmsIdGeneratorEnabled = true;
    private boolean sendGeneratedEvidencesToBackend = true;
    private final String identifierSuffix = "connector.ecodex.eu";
    private final String ebmsIdSuffix = "connector.ecodex.eu";
    private final ProcessingModeVerificationMode outboundMessageVerificationMode = ProcessingModeVerificationMode.STRICT;
    private final ProcessingModeVerificationMode inboundMessageVerificationMode = ProcessingModeVerificationMode.RELAXED;

    @Override
    public ConnectorMessageProcessingConfiguration getConfiguration() {
        return ConnectorMessageProcessingConfiguration
                .builder()
                .ebmsIdSuffix(ebmsIdSuffix)
                .identifierSuffix(identifierSuffix)
                .sendGeneratedEvidencesToBackend(sendGeneratedEvidencesToBackend)
                .inboundMessageVerificationMode(inboundMessageVerificationMode)
                .outboundMessageVerificationMode(outboundMessageVerificationMode)
                .ebmsIdGeneratorEnabled(ebmsIdGeneratorEnabled)
                .build();
    }
}
