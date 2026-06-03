/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.evidence;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import lombok.NonNull;

/**
 * Handles gateway confirmation messages that carry transported evidences only.
 *
 * <p>These messages update the lifecycle of the referenced business message and are forwarded
 * directly to the backend without entering the inbound business-message pipeline.
 */
public interface ConnectorInboundEvidenceMessageProcessor {
    /**
     * Applies transported evidences to the referenced business message and forwards the
     * confirmation message to the backend.
     *
     * @param confirmationMessage the persisted gateway confirmation message; must not be
     *                            {@code null}
     */
    void process(@NonNull ConnectorMessage confirmationMessage);
}
