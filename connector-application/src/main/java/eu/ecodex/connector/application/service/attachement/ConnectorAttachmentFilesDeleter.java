/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.attachement;

import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.service.message.ConnectorCleanupResolvedMessageAttachmentsService;
import eu.ecodex.connector.application.service.message.ConnectorCleanupUnresolvedMessagesAttachmentsService;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Deletes the files of attachments from the file storage and marks them as deleted.
 *
 * <p>This component holds the deletion logic shared by the attachment cleanups (see
 * {@link ConnectorCleanupOrphanedAttachmentsService},
 * {@link ConnectorCleanupUnresolvedMessagesAttachmentsService} and
 * {@link ConnectorCleanupResolvedMessageAttachmentsService}). For each attachment, the content is
 * removed from the file storage (see {@link ConnectorFileStorageProvider#deleteByIdentifier}), then
 * the attachments whose content was deleted are marked as deleted in the
 * {@link ConnectorMessageAttachmentRepository} (see
 * {@link ConnectorMessageAttachmentRepository#markAsDeleted}), using the {@link Clock} to record
 * the deletion instant.
 */
@Slf4j
@Service
public class ConnectorAttachmentFilesDeleter {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final Clock clock;

    /**
     * Creates the deleter.
     *
     * @param attachmentRepository the repository used to mark the attachments as deleted (must not
     *                             be null)
     * @param fileStorageProvider  the provider used to delete the content of the attachments from
     *                             the file storage (must not be null)
     * @param clock                the clock used to record the deletion instant, which allows a
     *                             fixed clock in tests (must not be null)
     */
    public ConnectorAttachmentFilesDeleter(
        ConnectorMessageAttachmentRepository attachmentRepository,
        ConnectorFileStorageProvider fileStorageProvider,
        Clock clock) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.clock = clock;
    }

    /**
     * Deletes the files of the active (not yet deleted) attachments of a message.
     */
    public Result deleteActiveAttachmentsOf(@NonNull String messageIdentifier) {
        var attachments = attachmentRepository.findAllActiveByMessageIdentifier(messageIdentifier);
        return delete(attachments);
    }

    /**
     * Deletes the files of the given attachments from the storage, then marks the successfully
     * deleted ones as deleted. A failure on one file does not stop the others: the failed
     * attachment is left untouched and will be picked up again by the next run.
     */
    public Result delete(@NonNull List<ConnectorMessageAttachment> attachments) {
        if (attachments.isEmpty()) {
            return Result.NONE;
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
                    "Could not delete attachment [{}] from [{}] storage",
                    attachment.identifier(), fileStorageProvider.getStorage(), e
                );
            }
        }

        if (!deleted.isEmpty()) {
            attachmentRepository.markAsDeleted(deleted, clock.instant());
        }

        return new Result(deleted.size(), failed);
    }

    /**
     * Outcome of a deletion: how many files were deleted and how many failed.
     */
    public record Result(int deleted, int failed) {
        public static final Result NONE = new Result(0, 0);

        public Result plus(Result other) {
            return new Result(deleted + other.deleted, failed + other.failed);
        }
    }
}
