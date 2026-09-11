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

import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundBusinessMessageStager;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.infrastructure.inbound.ConnectorEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for handling inbound message staging events.
 *
 * <p>This class implements {@link ConnectorEventHandler} and is triggered
 * asynchronously via a JMS queue when an inbound {@link ConnectorBusinessMessage} enters the
 * staging phase.
 */
@Slf4j
@Component
public class ConnectorJmsInboundMessageStagingListener
    implements ConnectorEventHandler<ConnectorBusinessMessage> {
    private final ConnectorInboundBusinessMessageStager messageStager;

    public ConnectorJmsInboundMessageStagingListener(
        ConnectorInboundBusinessMessageStager messageStager) {
        this.messageStager = messageStager;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.inbound-message-staging-queue}")
    public void handle(@NonNull ConnectorBusinessMessage message) {
        log.info("Entering inbound message [{}] staging process", message.identifier());
        messageStager.execute(message);
    }
}
