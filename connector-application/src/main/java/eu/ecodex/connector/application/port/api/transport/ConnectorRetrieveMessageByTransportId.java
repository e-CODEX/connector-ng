/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.transport;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Use case for retrieving a connector message by its transport identifier.
 */
public interface ConnectorRetrieveMessageByTransportId {
    /**
     * Retrieves the {@link ConnectorMessage} associated with the given transport identifier.
     *
     * @param transportIdentifier the identifier assigned to the message by the transport layer,
     *                            used to locate the message (must not be null)
     * @param linkPartnerName     name of the link partner updating the message
     *
     * @return the {@link ConnectorMessage} associated with the given transport identifier
     */
    ConnectorMessage execute(@Nonnull String transportIdentifier, @Nonnull String linkPartnerName);
}
