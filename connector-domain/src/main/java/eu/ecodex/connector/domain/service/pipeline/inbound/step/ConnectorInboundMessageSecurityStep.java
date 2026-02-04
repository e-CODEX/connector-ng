/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.inbound.step;

import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a processing step for handling the security aspects of inbound messages within the
 * connector system.
 */
@Slf4j
@DomainService
public class ConnectorInboundMessageSecurityStep implements ConnectorMessageStep {
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
    public ConnectorMessage execute(@NonNull ConnectorMessage inboundMessage) {
        log.debug(
                "processing inbound message ASIC-S container verification for: [{}]", inboundMessage
        );

        // validate the ASIC-S container
        this.securityToolkit.validateMessage(inboundMessage);

        log.debug("ASIC-S container verification completed for: [{}]", inboundMessage);

        return inboundMessage;
    }
}
