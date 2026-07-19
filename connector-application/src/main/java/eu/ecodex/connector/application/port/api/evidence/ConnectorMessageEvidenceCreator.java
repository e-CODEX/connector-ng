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
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import lombok.NonNull;

/**
 * Responsible for creating {@link ConnectorMessageEvidence} instances based on the outcome of
 * processing a {@link ConnectorMessage}.
 *
 * <p>This creator encapsulates the logic for generating evidence records that
 * represent either a successful or failed handling of a connector message. The resulting
 * {@link ConnectorMessageEvidence} typically contains metadata about the message and the type of
 * evidence being produced.</p>
 */
public interface ConnectorMessageEvidenceCreator {
    /**
     * Creates a {@link ConnectorMessageEvidence} representing successful processing of a
     * {@link ConnectorMessage}.
     *
     * @param evidenceType the type of evidence to create
     * @param message      the connector message that was successfully processed
     *
     * @return a {@link ConnectorMessageEvidence} instance representing the success
     */
    ConnectorMessageEvidence createSuccess(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorMessage message
    );

    /**
     * Creates a {@link ConnectorMessageEvidence} representing failed processing of a
     * {@link ConnectorMessage}.
     *
     * @param evidenceType the type of evidence to create
     * @param message      the connector message whose processing failed
     * @param reason       the reason why the message was rejected or failed
     *
     * @return a {@link ConnectorMessageEvidence} instance representing the failure
     */
    ConnectorMessageEvidence createFailure(
        @NonNull ConnectorEvidenceType evidenceType,
        @NonNull ConnectorMessage message,
        ConnectorMessageRejectionReason reason
    );
}
