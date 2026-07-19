/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.outbound;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;

/**
 * Defines the contract for staging a {@link ConnectorMessage} within a processing workflow.
 *
 * <p>Staging typically represents an intermediate persistence or preparation step before the
 * message proceeds to the next phase of processing (e.g. transmission, validation, or archival).
 *
 * <p>Implementations may store the message temporarily, update its processing state, or perform
 * additional preparation logic required by the connector pipeline.
 */
public interface ConnectorOutboundMessageStager {
    /**
     * Stages the given {@link ConnectorMessage}.
     *
     * @param message the message to stage; must not be {@code null}
     *
     * @throws RuntimeException if staging fails (implementations should document specific exception
     *                          types where appropriate)
     */
    void stage(ConnectorMessage message);
}
