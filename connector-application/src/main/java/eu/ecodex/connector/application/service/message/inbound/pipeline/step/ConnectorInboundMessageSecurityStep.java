/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound.pipeline.step;

import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.application.port.spi.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Processing step that handles the security aspects of inbound business messages within the
 * connector system.
 *
 * <p>The step delegates to a {@link ConnectorSecurityToolkit} to validate each incoming message
 * against the security policies and system requirements (see
 * {@link ConnectorSecurityToolkit#validateMessage}). It takes and returns a
 * {@link ConnectorBusinessMessage}, as defined by {@link ConnectorMessageStep}.
 */
@Slf4j
@Component
public class ConnectorInboundMessageSecurityStep
    implements ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> {
    private final ConnectorSecurityToolkit securityToolkit;

    /**
     * Creates the step.
     *
     * @param securityToolkit the toolkit used to validate incoming messages and ensure compliance
     *                        with the security policies and system requirements
     */
    public ConnectorInboundMessageSecurityStep(ConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull ConnectorBusinessMessage inboundMessage) {
        var messageIdentifier = inboundMessage.identifier();

        log.debug(
            "Processing inbound message [{}] ASIC-S container verification: ", messageIdentifier
        );

        // validate the ASIC-S container
        // unzip the ASIC-S container, set the business document, set the business attachments
        // remove the ASIC-S attachment from the list of attachments
        var validatedMessage = this.securityToolkit.validateMessage(inboundMessage);

        log.debug(
            "ASIC-S container verification completed for the message: [{}]", messageIdentifier
        );

        return validatedMessage;
    }
}
