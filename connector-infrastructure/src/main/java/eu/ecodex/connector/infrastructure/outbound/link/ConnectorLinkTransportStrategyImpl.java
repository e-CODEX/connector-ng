/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.link;

import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorLinkTransportStrategy}.
 */
@Slf4j
@Component
public class ConnectorLinkTransportStrategyImpl implements ConnectorLinkTransportStrategy {
    private final ConnectorMessageEventPublisher<ConnectorMessage> gatewayLinkEventPublisher;
    private final ConnectorMessageEventPublisher<ConnectorMessage> backendLinkEventPublisher;

    public ConnectorLinkTransportStrategyImpl(
        @Qualifier("connectorJmsGatewayLinkPublisher")
        ConnectorMessageEventPublisher<ConnectorMessage> gatewayLinkEventPublisher,
        @Qualifier("connectorJmsBackendLinkPublisher")
        ConnectorMessageEventPublisher<ConnectorMessage> backendLinkEventPublisher) {
        this.gatewayLinkEventPublisher = gatewayLinkEventPublisher;
        this.backendLinkEventPublisher = backendLinkEventPublisher;
    }

    @Override
    public void transport(@NonNull ConnectorMessage message) {
        if (message.direction() == ConnectorMessageDirection.BACKEND_TO_GATEWAY) {
            log.debug("Transporting message [{}] to the gateway", message.identifier());
            gatewayLinkEventPublisher.publish(message);
        } else {
            log.debug("Transporting message [{}] to the backend", message.identifier());
            backendLinkEventPublisher.publish(message);
        }
    }
}
