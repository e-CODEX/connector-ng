/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.port.api.message.ConnectorCleanupUnresolvedMessagesAttachments;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.application.service.attachement.ConnectorAttachmentFilesDeleter;
import java.time.Instant;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorCleanupUnresolvedMessagesAttachments}.
 */
@Slf4j
@Service
public class ConnectorCleanupUnresolvedMessagesAttachmentsService
    implements ConnectorCleanupUnresolvedMessagesAttachments {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorAttachmentFilesDeleter filesDeleter;

    /**
     * Creates the service.
     *
     * @param messageRepository the repository used to find the unresolved messages and to mark them
     *                          as deleted (must not be null)
     * @param filesDeleter      the component that deletes the content of the attachments from the
     *                          file storage and marks them as deleted (must not be null)
     */
    public ConnectorCleanupUnresolvedMessagesAttachmentsService(
        ConnectorMessageRepository messageRepository,
        ConnectorAttachmentFilesDeleter filesDeleter) {
        this.messageRepository = messageRepository;
        this.filesDeleter = filesDeleter;
    }

    @Override
    public void execute(@NonNull Instant threshold) {
        var unresolvedMessages = messageRepository.findAllUnresolved(threshold);
        if (unresolvedMessages.isEmpty()) {
            log.debug("No unresolved messages older than {}", threshold);
            return;
        }
        log.info(
            "{} unresolved messages older than {} to process",
            unresolvedMessages.size(), threshold
        );

        var total = ConnectorAttachmentFilesDeleter.Result.NONE;

        for (var message : unresolvedMessages) {
            try {
                var result = filesDeleter.deleteActiveAttachmentsOf(message.identifier());
                total = total.plus(result);

                if (result.failed() == 0) {
                    messageRepository.setAsDeleted(message.identifier());
                }
            } catch (RuntimeException e) {
                // e.g. database error: do not abort the other messages, retried at next run
                log.warn(
                    "Could not clean up the attachments of unresolved message [{}]",
                    message.identifier(), e
                );
            }
        }

        log.info(
            "Unresolved messages attachments cleanup done: [{}] deleted, [{}] failed",
            total.deleted(), total.failed()
        );
    }
}
