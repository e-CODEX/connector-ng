/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound;

import eu.ecodex.connector.application.port.api.businessdomain.ConnectorBusinessDomainVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorBusinessMessageVerifier;
import eu.ecodex.connector.application.port.api.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.propertiesprovider.ConnectorMessageProcessingConfigurationProvider;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service responsible for receiving and processing inbound evidence messages.
 */
@Slf4j
@Service
public class ConnectorInboundEvidenceMessageReceiverService implements
    ConnectorInboundEvidenceMessageReceiver {
    private final ConnectorBusinessDomainVerifier businessDomainVerifierService;
    private final ConnectorMessageProcessingConfigurationProvider configurationProvider;
    private final ConnectorBusinessMessageVerifier messageVerifierService;
    private final ConnectorMessageIdGenerator messageIdGeneratorService;
    private final ConnectorMessageEventPublisher<ConnectorEvidenceMessage>
        inboundEvidenceTriggerPublisher;

    /**
     * Constructs a new instance of {@code ConnectorInboundEvidenceMessageReceiverService}.
     *
     * @param businessDomainVerifierService   the service used to verify the business domain
     * @param configurationProvider           the provider of message processing configurations
     * @param messageVerifierService          the service for verifying business messages
     * @param messageIdGeneratorService       the service for generating message identifiers
     * @param inboundEvidenceTriggerPublisher the publisher responsible for triggering processing of
     *                                        inbound evidence messages
     */
    public ConnectorInboundEvidenceMessageReceiverService(
        ConnectorBusinessDomainVerifier businessDomainVerifierService,
        ConnectorMessageProcessingConfigurationProvider configurationProvider,
        ConnectorBusinessMessageVerifier messageVerifierService,
        ConnectorMessageIdGenerator messageIdGeneratorService,
        @Qualifier("connectorJmsInboundEvidenceTriggerPublisher")
        ConnectorMessageEventPublisher<ConnectorEvidenceMessage> inboundEvidenceTriggerPublisher) {
        this.businessDomainVerifierService = businessDomainVerifierService;
        this.configurationProvider = configurationProvider;
        this.messageVerifierService = messageVerifierService;
        this.messageIdGeneratorService = messageIdGeneratorService;
        this.inboundEvidenceTriggerPublisher = inboundEvidenceTriggerPublisher;
    }

    @Override
    public void execute(@NonNull ConnectorInboundEvidenceMessageCommand command) {
        log.debug("Received inbound evidence message command: {}", command);

        businessDomainVerifierService.execute(command.businessDomainIdentifier());

        var message = ConnectorEvidenceMessage
            .builder()
            .identifier(messageIdGeneratorService.execute())
            .businessDomainIdentifier(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID)
            .as4Properties(command.as4Properties())
            .direction(ConnectorMessageDirection.GATEWAY_TO_BACKEND)
            .gatewayName(ConnectorDefaults.DEFAULT_GATEWAY_NAME)
            .transportedEvidences(command.transportedEvidences())
            .build();

        var configuration = this.configurationProvider.getConfiguration();

        this.messageVerifierService.verify(
            message,
            configuration.inboundMessageVerificationMode()
        );

        this.inboundEvidenceTriggerPublisher.publish(message);
    }
}
