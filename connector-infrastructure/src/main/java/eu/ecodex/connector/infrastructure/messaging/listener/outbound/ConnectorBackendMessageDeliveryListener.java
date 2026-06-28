/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener.outbound;

import eu.ecodex.connector.application.service.usecase.transport.ConnectorRegisterMessageTransportStep;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.link.ConnectorLinkMode;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkPartnerRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.outbound.soap.ConnectorBackendDeliveryServiceClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for handling message submission to the backend.
 */
@Slf4j
@Component
@Transactional
public class ConnectorBackendMessageDeliveryListener implements ConnectorEventHandler {
    private final ConnectorRegisterMessageTransportStep messageTransportStep;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorBackendDeliveryServiceClient backendDeliveryServiceClient;
    private final ConnectorLinkPartnerRepository linkPartnerRepository;
    private final LegacyMessageHelper legacyMessageHelper;

    /**
     * Constructs a new instance of the {@code ConnectorBackendMessageDeliveryListener} class.
     *
     * @param messageTransportStep         Represents the transport step responsible for processing
     *                                     and executing message delivery within the connector
     *                                     registration process.
     * @param messageRepository            Repository for handling the persistence and retrieval of
     *                                     connector messages.
     * @param backendDeliveryServiceClient Client for interacting with backend services required for
     *                                     message delivery.
     * @param linkPartnerRepository        Repository for managing link partners associated with
     *                                     connector
     */
    public ConnectorBackendMessageDeliveryListener(
            ConnectorRegisterMessageTransportStep messageTransportStep,
            ConnectorMessageRepository messageRepository,
            ConnectorMessageEvidenceRepository evidenceRepository,
            ConnectorBackendDeliveryServiceClient backendDeliveryServiceClient,
            ConnectorLinkPartnerRepository linkPartnerRepository,
            LegacyMessageHelper legacyMessageHelper) {
        this.messageTransportStep = messageTransportStep;
        this.messageRepository = messageRepository;
        this.evidenceRepository = evidenceRepository;
        this.backendDeliveryServiceClient = backendDeliveryServiceClient;
        this.linkPartnerRepository = linkPartnerRepository;
        this.legacyMessageHelper = legacyMessageHelper;
    }

    @Override
    @JmsListener(destination = "${connector.queues.backend-delivery-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        if (message.identifier() == null) {
            throw new IllegalArgumentException("Message identifier cannot be null");
        }

        if (!message.isBusinessMessage() && !message.isEvidenceMessage()) {
            throw new IllegalStateException(
                    "Received message is neither evidence nor a business message"
            );
        }

        var partnerName = ConnectorLinkPartnerName.builder().name(message.backendName()).build();
        var linkPartner = this.linkPartnerRepository.findByName(partnerName);

        if (linkPartner == null) {
            throw new IllegalStateException("Link partner " + partnerName + " not found");
        }

        if (linkPartner.senderMode() == ConnectorLinkMode.PUSH) {
            submitToBackend(message);
        } else {
            makeReadyForPull(message);
        }
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

        var deliveryWebService = backendDeliveryServiceClient.createClient(message.backendName());

        try {
            var backendMessage = legacyMessageHelper.convertMessage(message);
            var acknowledgment = deliveryWebService.deliverMessage(backendMessage);

            if (acknowledgment.isResult()) {
                if (message.isBusinessMessage()) {
                    // TODO: also send SUBMISSION_CONFIRMATION back here ?
                    messageRepository.setDeliveredToBackendAt(identifier);
                    if (acknowledgment.getMessageId() != null) {
                        messageRepository.updateBackendIdentifier(
                                identifier,
                                acknowledgment.getMessageId()
                        );
                    }

                    // a business message has at least one transported evidence
                    message.transportedEvidences().forEach(
                            evidence ->
                                    evidenceRepository.setDeliveredToLinkPartnerAt(
                                            evidence.uuid())
                    );
                } else { // the message is an evidence message
                    var transportedEvidences = message.transportedEvidences();

                    if (transportedEvidences == null || transportedEvidences.isEmpty()) {
                        throw new IllegalStateException(
                                "The evidence message contains no transported evidence"
                        );
                    }

                    var transportedEvidence = transportedEvidences.getFirst();

                    evidenceRepository.setDeliveredToLinkPartnerAt(
                            transportedEvidence.uuid()
                    );
                }
                messageTransportStep.execute(
                        message,
                        ConnectorMessageTransportStatus.DELIVERED
                );

                log.info("Message [{}] delivered to the backend system", identifier);
            } else {
                log.error(
                        "Failed to deliver message [{}] to the backend system: [{}] ",
                        identifier,
                        acknowledgment.getResultMessage()
                );
                if (message.isBusinessMessage()) {
                    // TODO: if message is a business message and state is failed
                    // trigger NON_DELIVERY
                    messageRepository.setAsRejected(identifier);
                }
                messageTransportStep.execute(
                        message,
                        ConnectorMessageTransportStatus.FAILED
                );
            }
        } catch (Exception e) {
            log.error("Failed to deliver message [{}] to the backend system", identifier, e);
            messageTransportStep.execute(message, ConnectorMessageTransportStatus.FAILED);
        }
    }
}
