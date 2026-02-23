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
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link ConnectorUploadAttachments} responsible for handling the upload
 * workflow of connector attachments.
 */
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
        try {
            return files.stream()
                        .map(uploadCommand -> {
                            var name = StringUtils.stripFilenameExtension(uploadCommand.filename());
                            var identifier = String.format("%s_%s", UUID.randomUUID(), name);

                            var attachmentToSave = ConnectorMessageAttachment
                                    .builder()
                                    .identifier(identifier)
                                    .name(name)
                                    .size(uploadCommand.size())
                                    .contentType(uploadCommand.contentType())
                                    .description("Persisting file to S3 bucket")
                                    .storage(ConnectorAttachmentStorage.S3_BUCKET)
                                    .build();

                            var savedAttachments = this.attachmentRepository.save(attachmentToSave);

                            this.storageProvider.save(
                                    attachmentToSave,
                                    uploadCommand.inputStream()
                            );

                            return savedAttachments;
                        })
                        .toList();
        } catch (Exception e) {
            throw new ConnectorMessageAttachmentException(e.getMessage());
        }
    }
}
