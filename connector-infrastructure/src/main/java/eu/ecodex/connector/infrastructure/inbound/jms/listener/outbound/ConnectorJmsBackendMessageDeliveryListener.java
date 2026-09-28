/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.jms.listener.outbound;

import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageCommand;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.api.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkPartnerProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorEvidenceMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.inbound.ConnectorEventHandler;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorBackendDeliveryServiceClient;
import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for handling message submission to the backend.
 */
@Slf4j
@Component
@Transactional
public class ConnectorJmsBackendMessageDeliveryListener
    implements ConnectorEventHandler<ConnectorMessage> {
    private final ConnectorRegisterMessageTransportStep messageTransportStep;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorBackendDeliveryServiceClient backendDeliveryServiceClient;
    private final ConnectorLinkPartnerProvider linkPartnerProvider;
    private final LegacyMessageHelper legacyMessageHelper;
    private final ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService;

    @Value("${connector.message-processing.auto-trigger-delivery-evidences:false}")
    private boolean autoTriggerDeliveryEvidences;

    /**
     * Constructs a new instance of the {@code ConnectorBackendMessageDeliveryListener} class.
     *
     * @param messageTransportStep                   Represents the transport step responsible for
     *                                               processing and executing message delivery
     *                                               within the connector registration process.
     * @param messageRepository                      Repository for handling the persistence and
     *                                               retrieval of connector messages.
     * @param evidenceRepository                     Repository for handling the persistence and
     *                                               retrieval of connector evidences.
     * @param backendDeliveryServiceClient           Client for interacting with backend services
     *                                               required for message delivery.
     * @param linkPartnerProvider                    Repository for managing link partners
     *                                               associated with connector
     * @param legacyMessageHelper                    Helper for handling legacy messages.
     * @param outboundEvidenceMessageReceiverService Service for processing outbound evidence
     *                                               messages.
     */
    public ConnectorJmsBackendMessageDeliveryListener(
        ConnectorRegisterMessageTransportStep messageTransportStep,
        ConnectorMessageRepository messageRepository,
        ConnectorMessageEvidenceRepository evidenceRepository,
        ConnectorBackendDeliveryServiceClient backendDeliveryServiceClient,
        ConnectorLinkPartnerProvider linkPartnerProvider,
        LegacyMessageHelper legacyMessageHelper,
        ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageReceiverService) {
        this.messageTransportStep = messageTransportStep;
        this.messageRepository = messageRepository;
        this.evidenceRepository = evidenceRepository;
        this.backendDeliveryServiceClient = backendDeliveryServiceClient;
        this.linkPartnerProvider = linkPartnerProvider;
        this.legacyMessageHelper = legacyMessageHelper;
        this.outboundEvidenceMessageReceiverService = outboundEvidenceMessageReceiverService;
    }

    @Override
    @JmsListener(destination = "${connector.queues.backend-delivery-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        validate(message);

        var linkPartner = findLinkPartner(message.backendName());

        if (linkPartner.name().name().equals(ConnectorDefaults.DEFAULT_TEST_BACKEND_NAME)) {
            processTestMessage(message);
        } else if (linkPartner.senderMode() == ConnectorLinkMode.PUSH) {
            submitToBackend(message);
        } else {
            makeReadyForPull(message);
        }
    }

    private void validate(ConnectorMessage message) {
        if (!(message instanceof ConnectorBusinessMessage)
            && !(message instanceof ConnectorEvidenceMessage)) {
            throw unsupportedMessageType(message);
        }
    }

    private IllegalStateException unsupportedMessageType(ConnectorMessage message) {
        return new IllegalStateException(
            "Received message [%s] is neither evidence nor a business message : [%s]"
                .formatted(message.identifier(), message.getClass().getName())
        );
    }

    private ConnectorLinkPartner findLinkPartner(String backendName) {
        var partnerName = ConnectorLinkPartnerName.builder().name(backendName).build();
        var linkPartner = linkPartnerProvider.findByName(partnerName);

        if (linkPartner == null) {
            throw new IllegalStateException("Link partner %s not found".formatted(partnerName));
        }

        return linkPartner;
    }

    private void makeReadyForPull(ConnectorMessage message) {
        messageTransportStep.execute(
            message,
            ConnectorMessageTransportStatus.READY_FOR_DOWNLOAD
        );
        log.info("Message [{}] is ready for pull", message.identifier());
    }

    private void submitToBackend(@NonNull ConnectorMessage message) {
        var identifier = message.identifier();
        log.info("Submitting message [{}] to the backend system", identifier);

        DeliveryOutcome outcome;
        try {
            outcome = deliver(message);
        } catch (Exception e) {
            log.error("Failed to deliver message [{}] to the backend system", identifier, e);
            messageTransportStep.execute(message, ConnectorMessageTransportStatus.FAILED);
            return;
        }

        if (!outcome.accepted()) {
            handleRejection(message, outcome.resultMessage());
            return;
        }

        markAsDelivered(message, outcome.backendIdentifier());
        log.info("Message [{}] delivered to the backend system", identifier);
    }

    private DeliveryOutcome deliver(ConnectorMessage message) {
        var deliveryWebService = backendDeliveryServiceClient.createClient(message.backendName());
        var backendMessage = legacyMessageHelper.convertMessage(message);
        var acknowledgment = deliveryWebService.deliverMessage(backendMessage);

        return new DeliveryOutcome(
            acknowledgment.isResult(),
            acknowledgment.getMessageId(),
            acknowledgment.getResultMessage()
        );
    }

    private void markAsDelivered(ConnectorMessage message, @Nullable String backendIdentifier) {
        switch (message) {
            case ConnectorBusinessMessage businessMessage ->
                markBusinessMessageAsDelivered(businessMessage, backendIdentifier);
            case ConnectorEvidenceMessage evidenceMessage ->
                markEvidenceMessageAsDelivered(evidenceMessage);
            default -> throw unsupportedMessageType(message);
        }
        messageTransportStep.execute(message, ConnectorMessageTransportStatus.DELIVERED);
    }

    private void markBusinessMessageAsDelivered(
        ConnectorBusinessMessage message,
        @Nullable String backendIdentifier) {
        var identifier = message.identifier();

        if (autoTriggerDeliveryEvidences) {
            triggerDeliveryConfirmation(
                message.backendMessageIdentifier(),
                message.as4Properties().ebmsMessageIdentifier(),
                message.backendName()
            );
        }

        messageRepository.setDeliveredToLinkPartnerAt(identifier);

        if (backendIdentifier != null) {
            messageRepository.updateBackendIdentifier(identifier, backendIdentifier);
        }

        // a business message has at least one transported evidence
        var transportedEvidences = message.transportedEvidences();

        if (transportedEvidences != null && !transportedEvidences.isEmpty()) {
            transportedEvidences.forEach(
                evidence -> {
                    if (evidence.uuid() == null) {
                        throw new IllegalStateException(
                            "The evidence message contains no transported evidence");
                    }
                    evidenceRepository.setDeliveredToLinkPartnerAt(evidence.uuid());
                }
            );
        }
    }

    private void markEvidenceMessageAsDelivered(ConnectorEvidenceMessage message) {
        var transportedEvidences = message.transportedEvidences();

        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException(
                "The evidence message contains no transported evidence"
            );
        }

        var evidenceUuid = transportedEvidences.getFirst().uuid();

        if (evidenceUuid == null) {
            throw new IllegalStateException(
                "The evidence message contains no transported evidence"
            );
        }

        evidenceRepository.setDeliveredToLinkPartnerAt(evidenceUuid);
    }

    private void handleRejection(ConnectorMessage message, String reason) {
        var identifier = message.identifier();
        log.error(
            "Backend system rejected message [{}]: [{}]",
            identifier,
            reason
        );
        if (message instanceof ConnectorBusinessMessage) {
            // TODO: if message is a business message and state is failed
            // trigger NON_DELIVERY
            messageRepository.setAsRejected(identifier);
        }
        messageTransportStep.execute(message, ConnectorMessageTransportStatus.FAILED);
    }

    private void processTestMessage(@NonNull ConnectorMessage message) {
        var identifier = message.identifier();
        log.info("Processing test message [{}]", identifier);
        markAsDelivered(message, UUID.randomUUID().toString());
        log.info("Test message [{}] marked as delivered", message);
    }

    private void triggerDeliveryConfirmation(
        String backendMessageIdentifier,
        String referenceToIdentifier,
        String backendClientName) {

        var message = ConnectorOutboundEvidenceMessageCommand
            .builder()
            .evidenceType(ConnectorEvidenceType.DELIVERY)
            .backendMessageIdentifier(backendMessageIdentifier)
            .referenceToIdentifier(referenceToIdentifier)
            .backendName(backendClientName)
            .build();

        outboundEvidenceMessageReceiverService.execute(message);
    }


    private record DeliveryOutcome(
        boolean accepted,
        @Nullable String backendIdentifier,
        @Nullable String resultMessage
    ) {
    }
}
