/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.publisher;

import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Gateway Implementation of the {@link ConnectorEventPublisher}.
 */
@Slf4j
@Component("connectorGatewayLinkEventPublisher")
public class ConnectorGatewayLinkEventPublisher implements ConnectorEventPublisher {
    @Override
    public void publish(@NonNull ConnectorMessage message) {
        log.info("submitting message [{}] to gateway link", message.identifier());
        // TODO fake implementation. to be replaced with real implementation
    }
}
