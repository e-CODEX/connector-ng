/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.message.ConnectorMessageEbmsIdGeneratorService;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A processing step in the connector's outbound message workflow that creates and assigns a unique
 * EBMS message ID to the specified {@link ConnectorBusinessMessage}, if the EBMS ID generator is
 * enabled in the configuration.
 */
@Slf4j
@Component
public class ConnectorOutboundMessageEbmsIdStep
    implements ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> {
    private final ConnectorMessageEbmsIdGeneratorService messageEbmsIdGenerator;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider;

    /**
     * Construct a new {@link ConnectorOutboundMessageEbmsIdStep}.
     *
     * @param messageEbmsIdGenerator          the generator responsible for creating unique ebMS
     *                                        message identifiers
     * @param messageRepository               the repository used for managing message data,
     *                                        including updating the ebMS message identifier
     * @param processingConfigurationProvider provides message processing configuration, including
     *                                        whether ebMS ID generation is enabled
     */
    public ConnectorOutboundMessageEbmsIdStep(
        ConnectorMessageEbmsIdGeneratorService messageEbmsIdGenerator,
        ConnectorMessageRepository messageRepository,
        ConnectorMessageProcessingConfigurationProvider processingConfigurationProvider) {
        this.messageEbmsIdGenerator = messageEbmsIdGenerator;
        this.messageRepository = messageRepository;
        this.processingConfigurationProvider = processingConfigurationProvider;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull ConnectorBusinessMessage message) {
        var identifier = message.identifier();

        log.info("Creating EBMS ID for outbound message: [{}]", identifier);

        var configuration = this.processingConfigurationProvider.getConfiguration();

        if (configuration.ebmsIdGeneratorEnabled()) {
            log.info(
                "EBMS ID generator enabled, generating new EBMS message ID for "
                    + "outbound connector message: [{}]", identifier
            );

            return this.messageRepository.updateEbmsIdentifier(
                identifier, messageEbmsIdGenerator.generateIdentifier()
            );
        }

        log.info(
            "EBMS ID generator disabled, skipping EBMS message ID generation for "
                + "outbound connector message: [{}]",
            identifier
        );

        return message;
    }
}
