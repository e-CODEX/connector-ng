/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.jms.listener.inbound;

import eu.ecodex.connector.application.port.api.transport.ConnectorAckMessageTransportStep;
import eu.ecodex.connector.application.port.api.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for handling gateway message submission confirmation events.
 */
@Slf4j
@Component
@Transactional
public class ConnectorJmsGatewayMessageAcknowledgementListener {
    private final ConnectorMessageTransportStepRepository transportStepRepository;
    private final ConnectorAckMessageTransportStep ackMessageTransportStep;

    /**
     * Creates a new listener instance.
     *
     */
    public ConnectorJmsGatewayMessageAcknowledgementListener(
        ConnectorMessageTransportStepRepository transportStepRepository,
        ConnectorAckMessageTransportStep ackMessageTransportStep) {
        this.transportStepRepository = transportStepRepository;
        this.ackMessageTransportStep = ackMessageTransportStep;
    }

    /**
     * Handles gateway submission reply messages.
     *
     * <p>Extracts the message identifier and error detail from the JMS {@link MapMessage}, updates
     * the delivery status in the repository, and logs acknowledgement information.
     *
     * @param message the JMS {@link MapMessage} containing the gateway reply; must not be
     *                {@code null}
     *
     * @throws RuntimeException if parsing the JMS message fails
     */
    @JmsListener(destination = "${connector.queues.gateway-submission-reply-queue}")
    public void handle(@NonNull MapMessage message) {
        log.info("Receiving gateway message submission confirmation");

        try {
            var messageOrEbmsIdentifier = message.getStringProperty("messageId");

            if (messageOrEbmsIdentifier == null) {
                throw new IllegalArgumentException(
                    "Message identifier not found in gateway submission reply"
                );
            }

            log.info(
                "Gateway message submission confirmation received with the messageId: [{}]",
                messageOrEbmsIdentifier
            );

            var transportStep = transportStepRepository.findByMessageIdentifierOrRemoteSystemId(
                messageOrEbmsIdentifier);

            if (transportStep == null) {
                throw new IllegalStateException(
                    "No transport step found for the messageId: " + messageOrEbmsIdentifier
                );
            }

            var command = UpdateMessageTransportCommand
                .builder()
                .remoteMessageIdentifier(messageOrEbmsIdentifier)
                .status(ConnectorMessageTransportStatus.DELIVERED)
                .errors(null)
                .build();

            ackMessageTransportStep.execute(messageOrEbmsIdentifier, command);
        } catch (JMSException e) {
            throw new RuntimeException("Failed to parse Domibus reply", e);
        }
    }
}
