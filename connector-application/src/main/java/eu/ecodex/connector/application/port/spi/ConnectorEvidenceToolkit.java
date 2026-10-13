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
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Toolkit for creating {@link ConnectorMessageEvidence} instances representing the results of
 * operations performed on a {@link ConnectorBusinessMessage}.
 *
 * <p>The evidences are associated with a specific message and categorized by their
 * {@link ConnectorEvidenceType}. The same method creates evidences for successful operations
 * (without a rejection reason) and for failed operations (with a rejection reason).
 */
public interface ConnectorEvidenceToolkit {
    /**
     * Creates a {@link ConnectorMessageEvidence} for the given message, evidence type and optional
     * rejection reason.
     *
     * <p>The evidence represents the outcome of an operation performed on the message. A
     * {@code null} rejection reason denotes a successful operation, whereas a non-null reason
     * denotes a rejection or failure.
     *
     * @param message         the {@link ConnectorBusinessMessage} the evidence is linked to (must
     *                        not be null)
     * @param evidenceType    the {@link ConnectorEvidenceType} categorizing the evidence (must not
     *                        be null)
     * @param rejectionReason the {@link ConnectorMessageRejectionReason} describing why the message
     *                        was rejected; {@code null} if the evidence does not relate to a
     *                        rejection
     *
     * @return the created {@link ConnectorMessageEvidence} representing the operation result
     */
    ConnectorMessageEvidence create(
        @Nonnull ConnectorBusinessMessage message,
        @Nonnull ConnectorEvidenceType evidenceType,
        @Nullable ConnectorMessageRejectionReason rejectionReason
    );
}
