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
 * Represents a processing step for handling the security aspects of inbound messages within the
 * connector system.
 */
@Slf4j
@Component
public class ConnectorInboundMessageSecurityStep
    implements ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> {
    private final ConnectorSecurityToolkit securityToolkit;

    /**
     * Constructs a new instance of the {@code ConnectorInboundMessageSecurityStep} class.
     *
     * @param securityToolkit the toolkit responsible for handling security-related operations such
     *                        as validating incoming messages to ensure compliance with security
     *                        policies and system requirements within the connector system.
     */
    public ConnectorInboundMessageSecurityStep(ConnectorSecurityToolkit securityToolkit) {
        this.securityToolkit = securityToolkit;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull ConnectorBusinessMessage inboundMessage) {
        log.debug(
            "Processing inbound message [{}] ASIC-S container verification: ",
            inboundMessage.identifier()
        );

        // validate the ASIC-S container
        this.securityToolkit.validateMessage(inboundMessage);

        log.debug(
            "ASIC-S container verification completed for the message: [{}]",
            inboundMessage.identifier()
        );

        return inboundMessage;
    }
}
