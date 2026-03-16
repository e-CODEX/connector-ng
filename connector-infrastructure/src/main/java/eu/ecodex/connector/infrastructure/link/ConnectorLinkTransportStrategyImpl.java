/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.link;

import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.spi.link.ConnectorLinkTransportStrategy;
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
    private final ConnectorEventPublisher gatewayLinkEventPublisher;

    public ConnectorLinkTransportStrategyImpl(
            @Qualifier("connectorGatewayLinkEventPublisher")
            ConnectorEventPublisher gatewayLinkEventPublisher) {
        this.gatewayLinkEventPublisher = gatewayLinkEventPublisher;
    }

    @Override
    public void transport(@NonNull ConnectorMessage message) {
        if (message.direction() == ConnectorMessageDirection.BACKEND_TO_GATEWAY) {
            log.debug("transporting backend to gateway message: [{}]", message.identifier());

            gatewayLinkEventPublisher.publish(message);
        } else {
            log.debug("gateway to backend message transport strategy not yet implemented");
            // TODO to be implemented. no exception thrown to allow testing
        }
    }
}
