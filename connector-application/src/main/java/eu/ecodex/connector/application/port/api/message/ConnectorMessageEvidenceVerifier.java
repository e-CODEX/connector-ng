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

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import lombok.NonNull;

/**
 * Verifies that a connector business message satisfies the requirements for a given type of
 * evidence.
 */
public interface ConnectorMessageEvidenceVerifier {
    /**
     * Verifies that the given {@link ConnectorBusinessMessage} satisfies the requirements for the
     * given {@link ConnectorEvidenceType}.
     *
     * <p>The method returns normally if the message meets the requirements. If it does not, the
     * failure is reported by an exception.
     *
     * @param evidenceType the type of evidence the message is expected to support (must not be
     *                     null)
     * @param message      the business message to verify (must not be null)
     *
     * @throws RuntimeException if the message does not meet the validation criteria
     */
    void verify(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorBusinessMessage message);
}
