/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.outbound;

import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.impl.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.service.usecase.message.ConnectorMessageVerifier;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorOutboundMessageReceiver} service.
 */
@Slf4j
@Service
public class ConnectorOutboundMessageReceiverService implements ConnectorOutboundMessageReceiver {
    private final ConnectorMessageProcessingConfigurationProvider configurationProvider;
    private final ConnectorMessageVerifier messageVerifier;
    private final ConnectorEventPublisher stagingEventPublisher;
    private final ConnectorMessageIdGenerator messageIdGenerator;

    /**
     * Constructs a new {@code ConnectorOutboundMessageReceiverService}.
     *
     * @param configurationProvider provider of the current
     *                              {@link ConnectorMessageProcessingConfiguration}
     * @param messageVerifier       verifier used to validate outbound messages
     * @param stagingEventPublisher event publisher used to stage messages for further processing;
     *                              qualified as "connectorOutboundMessageStagingEventPublisher"
     * @param messageIdGenerator    generator used to assign unique identifiers to outbound
     *                              messages
     */
    public ConnectorOutboundMessageReceiverService(
            ConnectorMessageProcessingConfigurationProvider configurationProvider,
            ConnectorMessageVerifier messageVerifier,
            @Qualifier("connectorOutboundMessageStagingEventPublisher")
            ConnectorEventPublisher stagingEventPublisher,
            ConnectorMessageIdGenerator messageIdGenerator) {
        this.configurationProvider = configurationProvider;
        this.messageVerifier = messageVerifier;
        this.stagingEventPublisher = stagingEventPublisher;
        this.messageIdGenerator = messageIdGenerator;
    }

    @Override
    @Transactional
    public ConnectorMessage register(@NonNull final ConnectorMessage message) {
        var configuration = this.configurationProvider.getConfiguration();
        var messageWithId = this.assignIdentifier(message);
        this.messageVerifier.verify(messageWithId, configuration.outboundMessageVerificationMode());
        stagingEventPublisher.publish(messageWithId);

        return messageWithId;
    }

    private ConnectorMessage assignIdentifier(ConnectorMessage message) {
        log.debug("Assigning identifier to message [{}]", message);
        var identifier = this.messageIdGenerator.generateIdentifier();

        return message.toBuilder().identifier(identifier).build();
    }
}
