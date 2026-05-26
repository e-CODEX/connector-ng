/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence;

import eu.ecodex.connector.domain.api.ConnectorEvidenceToolkit;
import eu.ecodex.connector.domain.model.ConnectorMessageRejectionReason;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorEvidenceToolkit}.
 */
@Component
public class ConnectorEvidenceToolkitImpl implements ConnectorEvidenceToolkit {
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;

    public ConnectorEvidenceToolkitImpl(
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorFileStorageProvider fileStorageProvider) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public ConnectorMessageEvidence create(
            @NonNull ConnectorMessage message,
            @NonNull ConnectorEvidenceType evidenceType,
            @Nullable ConnectorMessageRejectionReason rejectionReason) {
        // TODO this is fake implementation. will be replaced with real implementation
        var evidenceContent = addAttachment(evidenceType);
        this.attachmentRepository.attachToMessage(
                evidenceContent.identifier(),
                message.identifier()
        );

        return ConnectorMessageEvidence
                .builder()
                .type(evidenceType)
                .attachment(evidenceContent)
                .build();
    }

    private ConnectorMessageAttachment addAttachment(ConnectorEvidenceType evidenceType) {
        var name = evidenceType.name();
        var identifier = String.format(
                "%s_%s", UUID.randomUUID(), name
        );
        var attachment = ConnectorMessageAttachment.builder()
                                                   .identifier(identifier)
                                                   .name(name + ".xml")
                                                   .contentType("text/xml")
                                                   .size("<xml />".getBytes().length)
                                                   .description("Evidence of " + name)
                                                   .storage(ConnectorAttachmentStorage.S3_BUCKET)
                                                   .type(ConnectorAttachmentType.EVIDENCE_XML)
                                                   .build();

        var savedEvidence = attachmentRepository.save(attachment);

        this.fileStorageProvider.save(savedEvidence, "<xml />".getBytes());

        return savedEvidence;
    }
}
