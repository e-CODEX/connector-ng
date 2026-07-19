/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.evidence;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Processes backend evidence trigger messages: creates signed REM evidence for a referenced
 * business message and submits the resulting evidence message to the gateway.
 */
public interface ConnectorEvidenceTriggerProcessor {
    /**
     * Handles an evidence trigger submitted by the backend.
     *
     * @param triggerMessage trigger payload (no business content, single evidence type, no XML)
     */
    void process(@Nonnull ConnectorMessage triggerMessage);
}
