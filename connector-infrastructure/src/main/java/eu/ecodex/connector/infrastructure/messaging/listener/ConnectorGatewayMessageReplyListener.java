/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener;

import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
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
public class ConnectorGatewayMessageReplyListener {
    private final ConnectorMessageRepository messageRepository;

    /**
     * Creates a new listener instance.
     *
     * @param messageRepository repository used to update message delivery status
     */
    public ConnectorGatewayMessageReplyListener(ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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
    @Transactional
    @JmsListener(destination = "${connector.queues.gateway-submission-reply-queue}")
    public void handle(@NonNull MapMessage message) {
        log.info("Receiving gateway message submission confirmation");

        try {
            var messageIdentifier = message.getStringProperty("messageId");

            if (messageIdentifier == null) {
                throw new RuntimeException(
                        "Message identifier not found in gateway submission reply");
            }

            log.info(
                    "Gateway message submission confirmation received for the message: [{}]",
                    messageIdentifier
            );
            this.messageRepository.setDeliveredToGatewayAt(messageIdentifier);
        } catch (JMSException e) {
            throw new RuntimeException("Failed to parse Domibus reply", e);
        }
    }
}
