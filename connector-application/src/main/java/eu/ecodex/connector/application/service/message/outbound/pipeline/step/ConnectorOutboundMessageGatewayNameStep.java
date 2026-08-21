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

import eu.ecodex.connector.application.port.api.message.pipeline.ConnectorMessageStep;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Represents a processing step in the outbound message workflow for the connector system,
 * specifically aimed at validating the gateway name of a {@link ConnectorBusinessMessage}. This
 * step is used to ensure that the message adheres to the required gateway naming conventions before
 * proceeding further in the processing pipeline.
 */
@Slf4j
@Component
public class ConnectorOutboundMessageGatewayNameStep
    implements ConnectorMessageStep<ConnectorBusinessMessage, ConnectorBusinessMessage> {
    private final ConnectorMessageRepository messageRepository;

    public ConnectorOutboundMessageGatewayNameStep(ConnectorMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public ConnectorBusinessMessage execute(@NonNull ConnectorBusinessMessage outboundMessage) {
        var identifier = outboundMessage.identifier();

        log.debug(
            "Processing outbound message [{}] gateway name validation",
            identifier
        );

        if (StringUtils.isNotEmpty(outboundMessage.gatewayName())) {
            return outboundMessage;
        }

        return this.messageRepository.updateGatewayName(
            identifier, ConnectorDefaults.DEFAULT_GATEWAY_NAME
        );
    }
}
