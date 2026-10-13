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
 * Defines the contract for staging an inbound {@link ConnectorBusinessMessage} within a processing
 * workflow.
 *
 * <p>Staging typically represents an intermediate persistence step before the message proceeds to
 * the next phase of processing.
 */
public interface ConnectorInboundBusinessMessageStager {
    /**
     * Stages the given inbound message.
     *
     * <p>The method returns normally if the message was staged successfully. If staging fails,
     * the failure is reported by an exception.
     *
     * @param message the message to stage; it encapsulates the identifiers, metadata, timestamps,
     *                direction and business content of the message (must not be null)
     */
    void execute(@Nonnull ConnectorBusinessMessage message);
}
