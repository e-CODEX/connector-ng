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

import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import jakarta.annotation.Nonnull;
import java.time.Instant;

/**
 * Use case for cleaning up the attachments of unresolved messages.
 *
 * <p>A message is unresolved when it has not reached a final state, that is, it is still waiting
 * for a transport or processing outcome (for example an acknowledgement or an evidence) after a
 * long time. The attachments of such messages are removed, since the message will not be processed
 * further. See {@link ConnectorCleanupUnresolvedMessagesAttachments} for the cleanup of the
 * messages themselves.
 */
public interface ConnectorCleanupUnresolvedMessagesAttachments {
    /**
     * Cleans up the attachments of the unresolved messages that have not been resolved since the
     * given threshold.
     *
     * <p>The threshold protects recent messages that may still be resolved normally: only the
     * attachments of unresolved messages older than the threshold are handled. For each of them,
     * the stored content is removed (see {@link ConnectorFileStorageProvider#deleteByIdentifier})
     * and the attachment is marked as deleted (see
     * {@link ConnectorMessageAttachmentRepository#markAsDeleted}).
     *
     * @param threshold the instant before which the attachments of unresolved messages are cleaned
     *                  up; messages created at or after this instant are left untouched (must not
     *                  be null)
     */
    void execute(@Nonnull Instant threshold);
}
