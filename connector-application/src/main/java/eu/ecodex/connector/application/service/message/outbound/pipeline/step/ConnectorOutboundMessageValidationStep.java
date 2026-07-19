/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound.pipeline.step;

import eu.ecodex.connector.application.port.api.message.ConnectorMessagePartiesVerifier;
import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Represents a processing step for checking outbound connector messages as part of the message
 * workflow.
 *
 * <p>This class implements {@link ConnectorMessageStep} and performs validations
 * for outgoing messages by using supporting services. It ensures that the messages adhere to the
 * required business and processing rules before they are moved further in the processing chain.
 */
@Slf4j
@Component
public class ConnectorOutboundMessageValidationStep implements ConnectorMessageStep {
    private final ConnectorMessagePartiesVerifier partiesVerifierService;

    public ConnectorOutboundMessageValidationStep(
        ConnectorMessagePartiesVerifier partiesVerifierService) {
        this.partiesVerifierService = partiesVerifierService;
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug("Processing outbound message [{}] validation", outboundMessage.identifier());

        this.partiesVerifierService.verify(outboundMessage);

        log.debug("Outbound message [{}] validation completed", outboundMessage.identifier());

        return outboundMessage;
    }
}
