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

import eu.ecodex.connector.application.service.usecase.evidence.ConnectorEvidenceTriggerProcessor;
import eu.ecodex.connector.domain.api.ConnectorEventHandler;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * JMS listener that processes backend evidence trigger messages.
 */
@Slf4j
@Component
public class ConnectorOutboundEvidenceTriggerListener implements ConnectorEventHandler {
    private final ConnectorEvidenceTriggerProcessor evidenceTriggerProcessor;

    public ConnectorOutboundEvidenceTriggerListener(
            ConnectorEvidenceTriggerProcessor evidenceTriggerProcessor) {
        this.evidenceTriggerProcessor = evidenceTriggerProcessor;
    }

    @Override
    @Transactional
    @JmsListener(destination = "${connector.queues.outbound-evidence-trigger-queue}")
    public void handle(@NonNull ConnectorMessage message) {
        log.info("Processing evidence trigger message [{}]", message.identifier());
        evidenceTriggerProcessor.process(message);
    }
}
