/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.attachement;

import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.domain.exception.ConnectorMessageAttachmentException;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ConnectorUploadAttachments} responsible for handling the upload
 * workflow of connector attachments.
 */
@Slf4j
@Service
@Transactional
public class ConnectorUploadAttachmentsService implements ConnectorUploadAttachments {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider storageProvider;

    public ConnectorUploadAttachmentsService(
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider storageProvider) {
        this.attachmentRepository = attachmentRepository;
        this.storageProvider = storageProvider;
    }

    @Override
    public List<ConnectorMessageAttachment> execute(@NonNull List<FileUploadCommand> files) {
        log.info("Uploading attachments");

        return files.stream()
                    .map(uploadCommand -> {
                        try {
                            var identifier = "%s-%s".formatted(
                                    UUID.randomUUID(), uploadCommand.filename()
                            );

                            var attachmentToSave = ConnectorMessageAttachment
                                    .builder()
                                    .identifier(identifier)
                                    .name(uploadCommand.filename())
                                    .size(uploadCommand.size())
                                    .contentType(uploadCommand.contentType())
                                    .description(uploadCommand.description())
                                    .storage(storageProvider.getStorage())
                                    .type(ConnectorAttachmentType.ATTACHMENT)
                                    .build();

                            var savedAttachments = this.attachmentRepository.save(attachmentToSave);

                            this.storageProvider.save(
                                    attachmentToSave,
                                    uploadCommand.tempFileLocation()
                            );

                            return savedAttachments;
                        } catch (Exception e) {
                            throw new ConnectorMessageAttachmentException(e.getMessage());
                        }
                    })
                    .toList();
    }
}
