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
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ConnectorCleanupUnresolvedMessagesAttachments}.
 */
@Slf4j
@Service
@Transactional
public class ConnectorCleanupUnresolvedMessagesAttachmentsService
    implements ConnectorCleanupUnresolvedMessagesAttachments {
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param messageRepository    the repository used to find the unresolved messages (must not be
     *                             null)
     * @param attachmentRepository the repository used to find the active attachments of those
     *                             messages and mark them as deleted (must not be null)
     * @param fileStorageProvider  the provider used to delete the content of the attachments from
     *                             the file storage (must not be null)
     * @param clock                the clock used to record the deletion instant, which allows a
     *                             fixed clock in tests (must not be null)
     */
    public ConnectorCleanupUnresolvedMessagesAttachmentsService(
        ConnectorMessageRepository messageRepository,
        ConnectorMessageAttachmentRepository attachmentRepository,
        ConnectorFileStorageProvider fileStorageProvider,
        Clock clock) {
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.clock = clock;
    }

    @Override
    public void execute(@NonNull Instant threshold) {
        var unresolvedMessages = messageRepository.findAllUnresolved(threshold);
        if (unresolvedMessages.isEmpty()) {
            log.debug("No unresolved messages older than {}", threshold);
            return;
        }
        log.info(
            "{} Unresolved messages older than {} to process", unresolvedMessages.size(), threshold
        );

        var totalDeleted = 0;
        var totalFailed = 0;

        for (var message : unresolvedMessages) {
            try {
                var result = cleanupAttachmentsOf(message.identifier());
                totalDeleted += result.deleted();
                totalFailed += result.failed();
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
            totalDeleted, totalFailed
        );
    }

    private CleanupResult cleanupAttachmentsOf(String messageIdentifier) {
        var attachments = attachmentRepository.findAllActiveByMessageIdentifier(messageIdentifier);
        if (attachments.isEmpty()) {
            return CleanupResult.NONE;
        }

        var deleted = new ArrayList<String>(attachments.size());
        var failed = 0;

        for (var attachment : attachments) {
            try {
                fileStorageProvider.deleteByIdentifier(attachment.identifier());
                deleted.add(attachment.identifier());
            } catch (RuntimeException e) {
                failed++;
                log.warn(
                    "Could not delete attachment [{}] of unresolved message [{}] from [{}] storage",
                    attachment.identifier(), messageIdentifier, fileStorageProvider.getStorage(), e
                );
            }
        }

        if (!deleted.isEmpty()) {
            attachmentRepository.markAsDeleted(deleted, clock.instant());
        }

        log.debug(
            "Message [{}]: [{}] attachments deleted, [{}] failed",
            messageIdentifier, deleted.size(), failed
        );

        return new CleanupResult(deleted.size(), failed);
    }

    private record CleanupResult(int deleted, int failed) {
        static final CleanupResult NONE = new CleanupResult(0, 0);
    }
}
