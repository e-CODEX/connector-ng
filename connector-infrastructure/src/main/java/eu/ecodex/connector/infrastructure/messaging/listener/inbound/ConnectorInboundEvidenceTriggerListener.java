/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener.inbound;

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorInboundEvidenceMessageProcessor;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener that processes gateway evidence trigger messages.
 */
@Slf4j
@Component
public class ConnectorInboundEvidenceTriggerListener implements ConnectorEventHandler {
    private final ConnectorInboundEvidenceMessageProcessor confirmationMessageProcessor;

    public ConnectorInboundEvidenceTriggerListener(
            ConnectorInboundEvidenceMessageProcessor confirmationMessageProcessor) {
        this.confirmationMessageProcessor = confirmationMessageProcessor;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.inbound-evidence-trigger-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        log.info("Processing gateway evidence trigger message [{}]", message.identifier());
        confirmationMessageProcessor.process(message);
    }
}
