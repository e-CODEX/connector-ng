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

import eu.ecodex.connector.application.port.api.attachment.ConnectorCleanupOrphanedAttachments;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ConnectorCleanupOrphanedAttachments}.
 *
 * <p>The service finds the attachments that were uploaded but never linked to a message and
 * created before the given threshold, then deletes them. It is called by
 * {@code ConnectorOrphanedAttachmentsCleanupJob}.
 */
@Slf4j
@Service
@Transactional
public class ConnectorCleanupOrphanedAttachmentsService implements
    ConnectorCleanupOrphanedAttachments {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param attachmentRepository the repository used to find the orphaned attachments and mark
     *                             them as deleted
     * @param fileStorageProvider  the provider used to delete the content of the attachments from
     *                             the file storage
     * @param clock                the clock used to record the deletion instant, which allows a
     *                             fixed clock in tests
     */
    public ConnectorCleanupOrphanedAttachmentsService(
        ConnectorMessageAttachmentRepository attachmentRepository,
        ConnectorFileStorageProvider fileStorageProvider, Clock clock) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.clock = clock;
    }

    @Override
    public void execute(@NonNull Instant threshold) {
        var orphanedAttachments = attachmentRepository.findAllOrphaned(threshold);
        if (orphanedAttachments.isEmpty()) {
            log.debug("No orphaned attachments older than {}", threshold);
            return;
        }
        log.info("{} orphaned attachments to delete", orphanedAttachments.size());

        var deleted = new ArrayList<String>();
        var failed = 0;
        for (var orphaned : orphanedAttachments) {
            try {
                fileStorageProvider.deleteByIdentifier(orphaned.identifier());
                deleted.add(orphaned.identifier());
            } catch (RuntimeException e) {
                failed++;
                log.warn(
                    "Could not delete orphaned attachment [{}] from [{}] storage",
                    orphaned.identifier(), fileStorageProvider.getStorage(), e
                );
            }
        }

        if (!deleted.isEmpty()) {
            attachmentRepository.markAsDeleted(deleted, clock.instant());
        }
        log.info(
            "Orphaned attachments cleanup done: [{}] deleted, [{}] failed",
            deleted.size(),
            failed
        );
    }
}
