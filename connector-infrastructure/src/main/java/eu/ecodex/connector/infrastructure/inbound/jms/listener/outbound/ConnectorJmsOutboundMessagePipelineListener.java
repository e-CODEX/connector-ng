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

import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessagePipeline;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.infrastructure.inbound.ConnectorEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener responsible for triggering the outbound message processing pipeline when a
 * {@link ConnectorMessage} processing event is received from the backend.
 *
 * <p>This component listens to the configured outbound message processing queue and delegates the
 * received message to the {@code connectorOutboundMessagePipeline}. The pipeline then executes the
 * configured sequence of processing steps for outbound connector messages.
 *
 * <p>The listener operates within a transactional context to ensure that message consumption and
 * later processing are handled atomically according to the application's transaction
 * configuration.
 */
@Slf4j
@Component
public class ConnectorJmsOutboundMessagePipelineListener implements ConnectorEventHandler {
    private final ConnectorMessagePipeline outboundMessagePipeline;

    public ConnectorJmsOutboundMessagePipelineListener(
        @Qualifier("connectorOutboundMessagePipeline")
        ConnectorMessagePipeline outboundMessagePipeline) {
        this.outboundMessagePipeline = outboundMessagePipeline;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.outbound-message-processing-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        log.info("Entering outbound message [{}] processing pipeline ", message.identifier());
        this.outboundMessagePipeline.process(message);
    }
}
