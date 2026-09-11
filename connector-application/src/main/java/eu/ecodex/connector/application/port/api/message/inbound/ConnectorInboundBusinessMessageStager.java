/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.inbound;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for staging inbound business messages.
 */
public interface ConnectorInboundBusinessMessageStager {
    /**
     * Executes the processing logic for the provided business message.
     *
     * @param message The {@link ConnectorBusinessMessage} to be staged or processed. This object
     *                encapsulates all relevant details such as identifiers, metadata, timestamps,
     *                directional flow, and business content. The message must not be null.
     */
    void execute(@Nonnull ConnectorBusinessMessage message);
}
