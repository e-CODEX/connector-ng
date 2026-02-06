/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security;

import eu.ecodex.connector.domain.api.ConnectorSecurityToolkit;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorSecurityToolkit}.
 */
@Component
public class ConnectorSecurityToolkitImpl implements ConnectorSecurityToolkit {
    @Override
    public void validateMessage(@NonNull ConnectorMessage message) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage buildContainer(@NonNull ConnectorMessage message) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
