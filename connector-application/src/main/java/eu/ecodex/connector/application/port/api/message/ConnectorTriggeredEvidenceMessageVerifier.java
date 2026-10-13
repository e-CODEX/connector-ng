/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.message.ConnectorTriggeredEvidenceMessage;
import jakarta.annotation.Nonnull;

/**
 * Verifies a {@link ConnectorTriggeredEvidenceMessage}, that is, an evidence message triggered for
 * a connector message.
 */
public interface ConnectorTriggeredEvidenceMessageVerifier {
    /**
     * Verifies the given triggered evidence message.
     *
     * <p>The method returns normally if the verification succeeds. If it fails, the failure is
     * reported by an exception.
     *
     * @param triggeredEvidenceMessage the triggered evidence message to verify (must not be null)
     */
    void verify(@Nonnull ConnectorTriggeredEvidenceMessage triggeredEvidenceMessage);
}
