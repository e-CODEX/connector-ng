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

import eu.ecodex.connector.domain.api.link.ConnectorLinkTransportStrategy;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorLinkTransportStrategy}.
 */
@Component
public class ConnectorLinkTransportStrategyImpl implements ConnectorLinkTransportStrategy {
    @Override
    public void process(
            @NonNull ConnectorMessage message,
            @NonNull ConnectorLinkPartner linkPartner) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
