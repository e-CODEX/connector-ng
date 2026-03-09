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

import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageStager;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for handling outbound message staging events.
 *
 * <p>This class implements {@link ConnectorEventHandler} and is triggered
 * asynchronously via a JMS queue when an outbound {@link ConnectorMessage} enters the staging
 * phase.
 *
 * <p>The listener is expected to process the staged message and initiate
 * further outbound handling steps (e.g. validation, transformation, AS4 dispatch, etc.).
 */
@Slf4j
@Component
public class ConnectorOutboundMessageStagingListener implements ConnectorEventHandler {
    private final ConnectorOutboundMessageStager messageStager;

    public ConnectorOutboundMessageStagingListener(
            ConnectorOutboundMessageStager messageStager) {
        this.messageStager = messageStager;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.outbound-message-staging-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        log.info("received outbound message staging event: [{}]", message);
        messageStager.stage(message);
    }
}
