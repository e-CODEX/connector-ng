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

import eu.ecodex.connector.application.exception.ConnectorMessageAttachmentException;
import eu.ecodex.connector.application.exception.NotFoundException;
import eu.ecodex.connector.application.port.api.attachment.ConnectorDownloadAttachment;
import eu.ecodex.connector.application.port.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ConnectorDownloadAttachment} responsible for handling the
 * download of connector attachment.
 */
@Service
public class ConnectorDownloadAttachmentService implements ConnectorDownloadAttachment {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider storageProvider;

    public ConnectorDownloadAttachmentService(
        ConnectorMessageAttachmentRepository attachmentRepository,
        ConnectorFileStorageProvider storageProvider) {
        this.attachmentRepository = attachmentRepository;
        this.storageProvider = storageProvider;
    }

    @Override
    public byte[] execute(@NonNull String identifier) {
        var attachment = this.attachmentRepository.findByIdentifier(identifier);

        if (attachment == null) {
            throw new NotFoundException("Attachment not found");
        }

        var document = this.storageProvider.findByIdentifier(attachment.identifier());

        if (document == null) {
            throw new ConnectorMessageAttachmentException(
                "Attachment is no longer available in the storage"
            );
        }

        return document;
    }
}
