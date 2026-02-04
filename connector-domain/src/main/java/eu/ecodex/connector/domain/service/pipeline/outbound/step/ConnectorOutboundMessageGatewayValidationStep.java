/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.service.pipeline.outbound.step;

import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.annotation.DomainService;
import eu.ecodex.connector.domain.api.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a processing step in the outbound message workflow for the connector system,
 * specifically aimed at validating the gateway name of a {@link ConnectorMessage}. This step is
 * used to ensure that the message adheres to the required gateway naming conventions before
 * proceeding further in the processing pipeline.
 */
@Slf4j
@DomainService
public class ConnectorOutboundMessageGatewayValidationStep implements ConnectorMessageStep {
    /**
     * Constructs a new instance of {@code ConnectorOutboundMessageGatewayValidationStep}.
     */
    public ConnectorOutboundMessageGatewayValidationStep() {
    }

    @Override
    public ConnectorMessage execute(@NonNull ConnectorMessage outboundMessage) {
        log.debug("processing outbound message gateway name validation for: [{}]", outboundMessage);

        if (StringUtils.isNotEmpty(outboundMessage.gatewayName())) {
            return outboundMessage;
        }

        return outboundMessage.toBuilder().gatewayName(
                ConnectorDefaults.DEFAULT_GATEWAY_NAME).build();
    }
}
