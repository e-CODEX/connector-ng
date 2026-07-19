/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi;

import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Interface for creating instances of {@link ConnectorMessageEvidence} to represent the results of
 * operations performed on {@link ConnectorMessage}.
 *
 * <p>This toolkit provides methods to create evidence objects for both successful and failed
 * operations. These evidences are associated with specific message operations and categorized by
 * their respective {@link ConnectorEvidenceType}.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Creating success evidence for operations that complete successfully.</li>
 *     <li>
 *         Creating failure evidence for operations that fail along with detailed rejection reasons.
 *     </li>
 * </ul>
 */
public interface ConnectorEvidenceToolkit {
    /**
     * Creates an instance of {@link ConnectorMessageEvidence} based on the provided message,
     * evidence type, and optional rejection reason. The evidence represents the outcome of an
     * operation performed on a message within the connector domain.
     *
     * @param message         The {@link ConnectorMessage} object to which the evidence is linked.
     *                        Must not be null.
     * @param evidenceType    The {@link ConnectorEvidenceType} categorizing the type of evidence.
     *                        Must not be null.
     * @param rejectionReason The {@link ConnectorMessageRejectionReason} describing the reason for
     *                        rejection, if applicable. Can be null if the evidence does not relate
     *                        to a rejection.
     *
     * @return A {@link ConnectorMessageEvidence} instance representing the operation result.
     */
    ConnectorMessageEvidence create(
        @Nonnull ConnectorMessage message,
        @Nonnull ConnectorEvidenceType evidenceType,
        @Nullable ConnectorMessageRejectionReason rejectionReason
    );
}
