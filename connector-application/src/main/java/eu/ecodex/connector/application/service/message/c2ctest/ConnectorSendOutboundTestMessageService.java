/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.c2ctest;

import eu.ecodex.connector.application.exception.ConnectorTestMessageDisabledException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorBusinessMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorSendOutboundTestMessage;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageAS4PropertiesCommand;
import eu.ecodex.connector.application.port.api.message.c2ctest.ConnectorTestBusinessMessageCommand;
import eu.ecodex.connector.application.port.api.pmode.ConnectorProcessingModeVerifier;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.ConnectorTestMessageConfigurationProvider;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for sending test messages through the connector system.
 */
@Slf4j
@Service
@Transactional
public class ConnectorSendOutboundTestMessageService implements ConnectorSendOutboundTestMessage {
    private final ConnectorTestMessageConfigurationProvider testMessageConfigProvider;
    private final ConnectorMessageProcessingConfigurationProvider messageProcessingConfigProvider;
    private final ConnectorMessageIdGenerator messageIdGeneratorService;
    private final ConnectorBusinessDomainVerifier businessDomainVerifierService;
    private final ConnectorProcessingModeVerifier processingModeVerifierService;
    private final ConnectorBusinessMessageVerifier messageVerifierService;
    private final ConnectorMessageEventPublisher<ConnectorBusinessMessage> stagingEventPublisher;

    /**
     * Creates a new service for sending connector test messages.
     *
     * @param testMessageConfigProvider       Provides the configuration for connector test
     *                                        messages.
     * @param messageProcessingConfigProvider Provides the message processing configuration.
     * @param messageIdGeneratorService       Generates unique identifiers for messages.
     * @param businessDomainVerifierService   Verifies business domain information associated with
     *                                        messages.
     * @param processingModeVerifierService   Verifies the configured processing mode for messages.
     * @param messageVerifierService          Verifies business messages before they are sent.
     * @param stagingEventPublisher           Publishes business messages to the outbound staging
     *                                        area.
     */

    public ConnectorSendOutboundTestMessageService(
        ConnectorTestMessageConfigurationProvider testMessageConfigProvider,
        ConnectorMessageProcessingConfigurationProvider messageProcessingConfigProvider,
        ConnectorMessageIdGenerator messageIdGeneratorService,
        ConnectorBusinessDomainVerifier businessDomainVerifierService,
        ConnectorProcessingModeVerifier processingModeVerifierService,
        ConnectorBusinessMessageVerifier messageVerifierService,
        @Qualifier("connectorJmsOutboundMessageStagingPublisher")
        ConnectorMessageEventPublisher<ConnectorBusinessMessage> stagingEventPublisher) {
        this.testMessageConfigProvider = testMessageConfigProvider;
        this.messageProcessingConfigProvider = messageProcessingConfigProvider;
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.businessDomainVerifierService = businessDomainVerifierService;
        this.processingModeVerifierService = processingModeVerifierService;
        this.messageVerifierService = messageVerifierService;
        this.stagingEventPublisher = stagingEventPublisher;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull ConnectorTestBusinessMessageCommand command) {
        log.info("Sending Connector2Connector test message");

        var testMessageConfiguration = testMessageConfigProvider.getConfig();

        if (!testMessageConfiguration.enabled()) {
            throw new ConnectorTestMessageDisabledException("Test message is disabled");
        }

        businessDomainVerifierService.execute(command.businessDomainIdentifier());
        processingModeVerifierService.execute(command.businessDomainIdentifier());

        var message = ConnectorBusinessMessage
            .builder()
            .identifier(messageIdGeneratorService.execute())
            .businessDomainIdentifier(command.businessDomainIdentifier())
            .backendMessageIdentifier(command.backendMessageIdentifier())
            .backendName(ConnectorDefaults.DEFAULT_TEST_BACKEND_NAME)
            .as4Properties(toAS4Properties(command.as4PropertiesCommand()))
            .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
            .businessContent(command.businessContent())
            .attachments(command.attachments())
            .build();

        var configuration = messageProcessingConfigProvider.getConfiguration();
        messageVerifierService.verify(
            message,
            configuration.outboundMessageVerificationMode()
        );

        stagingEventPublisher.publish(message);

        return message;
    }

    private ConnectorMessageAS4Properties toAS4Properties(
        ConnectorTestBusinessMessageAS4PropertiesCommand as4PropertiesCommand) {
        var testMessageConfiguration = testMessageConfigProvider.getConfig();
        var action = ConnectorAction
            .builder()
            .name(testMessageConfiguration.action())
            .build();
        var service = ConnectorService
            .builder()
            .name(testMessageConfiguration.service().name())
            .type(testMessageConfiguration.service().type())
            .build();

        return ConnectorMessageAS4Properties
            .builder()
            .originalSender(as4PropertiesCommand.originalSender())
            .finalRecipient(as4PropertiesCommand.finalRecipient())
            .ebmsMessageIdentifier(as4PropertiesCommand.ebmsIdentifier())
            .conversationIdentifier(as4PropertiesCommand.conversationIdentifier())
            .service(service)
            .action(action)
            .fromParty(as4PropertiesCommand.fromParty())
            .toParty(as4PropertiesCommand.toParty())
            .build();
    }
}
