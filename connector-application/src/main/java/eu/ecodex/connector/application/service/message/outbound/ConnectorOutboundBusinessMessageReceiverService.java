/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorBusinessMessageVerifier;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfiguration;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.application.service.message.ConnectorMessageIdGeneratorService;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorOutboundBusinessMessageReceiver} service.
 */
@Slf4j
@Service
@Transactional
public class ConnectorOutboundBusinessMessageReceiverService
    implements ConnectorOutboundBusinessMessageReceiver {
    private final ConnectorMessageProcessingConfigurationProvider configurationProvider;
    private final ConnectorBusinessMessageVerifier messageVerifierService;
    private final ConnectorMessageEventPublisher<ConnectorBusinessMessage> stagingEventPublisher;
    private final ConnectorMessageIdGeneratorService messageIdGeneratorService;
    private final ConnectorBusinessDomainVerifier businessDomainVerifierService;

    /**
     * Constructs a new {@code ConnectorOutboundMessageReceiverService}.
     *
     * @param configurationProvider         provider of the current
     *                                      {@link ConnectorMessageProcessingConfiguration}
     * @param messageVerifierService        verifier used to validate outbound messages
     * @param stagingEventPublisher         event publisher used to stage messages for further
     *                                      processing; qualified as
     *                                      "connectorOutboundMessageStagingEventPublisher"
     * @param messageIdGeneratorService     generator used to assign unique identifiers to outbound
     *                                      messages param businessDomainVerifier verifier used to
     *                                      validate business domains of outbound messages
     * @param businessDomainVerifierService verifier used to validate business domains of outbound
     *                                      messages
     */
    public ConnectorOutboundBusinessMessageReceiverService(
        ConnectorMessageProcessingConfigurationProvider configurationProvider,
        ConnectorBusinessMessageVerifier messageVerifierService,
        @Qualifier("connectorJmsOutboundMessageStagingPublisher")
        ConnectorMessageEventPublisher<ConnectorBusinessMessage> stagingEventPublisher,
        ConnectorMessageIdGeneratorService messageIdGeneratorService,
        ConnectorBusinessDomainVerifier businessDomainVerifierService) {
        this.configurationProvider = configurationProvider;
        this.messageVerifierService = messageVerifierService;
        this.stagingEventPublisher = stagingEventPublisher;
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.businessDomainVerifierService = businessDomainVerifierService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorOutboundBusinessMessageCommand command) {
        businessDomainVerifierService.execute(command.businessDomainIdentifier());

        var message = ConnectorBusinessMessage
            .builder()
            .identifier(this.messageIdGeneratorService.generateIdentifier())
            .businessDomainIdentifier(command.businessDomainIdentifier())
            .backendMessageIdentifier(command.backendMessageIdentifier())
            .referenceToBackendMessageIdentifier(command.referenceToBackendMessageIdentifier())
            .backendName(command.backendName())
            .as4Properties(command.as4Properties())
            .direction(command.direction())
            .businessContent(command.businessContent())
            .attachments(command.attachments())
            .build();

        var configuration = this.configurationProvider.getConfiguration();
        this.messageVerifierService.verify(
            message,
            configuration.outboundMessageVerificationMode()
        );
        stagingEventPublisher.publish(message);

        return message;
    }
}
