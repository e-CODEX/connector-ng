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

import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import lombok.NonNull;

/**
 * Factory for creating {@link ConnectorMessageEvidence} objects for connector business messages.
 */
public interface ConnectorMessageEvidenceCreator {
    /**
     * Creates an evidence of the given type attesting that the given message was processed
     * successfully.
     *
     * @param evidenceType type of the evidence to create (must not be null)
     * @param message      the business message the evidence relates to (must not be null)
     *
     * @return the created {@link ConnectorMessageEvidence}
     */
    ConnectorMessageEvidence createSuccess(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorBusinessMessage message
    );

    /**
     * Creates an evidence of the given type attesting that the given message was rejected or
     * failed.
     *
     * @param evidenceType type of the evidence to create (must not be null)
     * @param message      the business message the evidence relates to (must not be null)
     * @param reason       the reason why the message was rejected; may be {@code null} if no
     *                     specific reason is available
     *
     * @return the created {@link ConnectorMessageEvidence}, carrying the rejection reason
     */
    ConnectorMessageEvidence createFailure(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorBusinessMessage message,
        ConnectorMessageRejectionReason reason
    );
}
