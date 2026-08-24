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
 * Verifies that a {@link ConnectorBusinessMessage} is valid for a given
 * {@link ConnectorEvidenceType}.
 *
 * <p>This component performs validation checks before evidence is created or
 * processed. Implementations may ensure that the message contains the required information,
 * structure, or metadata expected for the specified evidence type.</p>
 *
 * <p>If the verification fails, an exception should be thrown to indicate that
 * the message cannot be used to generate or process the requested evidence.</p>
 */
public interface ConnectorMessageEvidenceVerifier {
    /**
     * Verifies that the provided {@link ConnectorBusinessMessage} satisfies the requirements for
     * the given {@link ConnectorEvidenceType}.
     *
     * @param evidenceType the type of evidence the message is expected to support
     * @param message      the connector message to verify
     *
     * @throws RuntimeException if the message does not meet the validation criteria
     */
    void verify(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorBusinessMessage message);
}
