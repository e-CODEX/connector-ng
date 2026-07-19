/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.link;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Service interface for handling the submission of link messages within the system.
 *
 * <p>Implementations of this interface are responsible for processing and submitting
 * instances of {@code ConnectorMessage} to the appropriate endpoint or system.
 *
 * <p>The submission process may involve:
 * - Validation of the message content. - Transformation or preparation of the message for dispatch.
 * - Interfacing with external systems or services for message delivery.
 *
 * <p>Typical usage scenarios include:
 * - Submitting messages from a backend system to a gateway. - Forwarding processed messages within
 * the connector system.
 *
 * <p>The implementation should define the behaviour and outcomes of the submission process.
 */
public interface ConnectorLinkSubmitter {
    /**
     * Submits a specified {@link ConnectorMessage} for processing and delivery. The method is
     * responsible for handling the message submission to the appropriate endpoint or system,
     * potentially involving validation, transformation, or preparation.
     *
     * @param message the {@link ConnectorMessage} instance to be submitted; must not be null.
     */
    void submit(@Nonnull ConnectorMessage message);
}
