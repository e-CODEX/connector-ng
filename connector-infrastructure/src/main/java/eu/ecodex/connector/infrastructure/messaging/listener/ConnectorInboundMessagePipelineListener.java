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

import eu.ecodex.connector.application.service.impl.message.inbound.pipeline.ConnectorInboundMessagePipeline;
import eu.ecodex.connector.application.service.usecase.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for triggering the inbound message processing pipeline when a
 * {@link ConnectorMessage} processing event is received from the gateway.
 *
 * <p>This component listens to the configured inbound message processing queue and delegates the
 * received message to the {@link  ConnectorInboundMessagePipeline}. The pipeline then executes the
 * configured sequence of processing steps for inbound connector messages.
 *
 * <p>The listener operates within a transactional context to ensure that message consumption and
 * later processing are handled atomically according to the application's transaction
 * configuration.
 */
@Slf4j
@Component
public class ConnectorInboundMessagePipelineListener implements ConnectorEventHandler {
    private final ConnectorMessagePipeline inboundMessagePipeline;

    public ConnectorInboundMessagePipelineListener(
            @Qualifier("connectorInboundMessagePipeline")
            ConnectorMessagePipeline inboundMessagePipeline) {
        this.inboundMessagePipeline = inboundMessagePipeline;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.inbound-message-processing-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        log.info("received inbound message processing event: [{}]", message);
        this.inboundMessagePipeline.process(message);
    }
}
