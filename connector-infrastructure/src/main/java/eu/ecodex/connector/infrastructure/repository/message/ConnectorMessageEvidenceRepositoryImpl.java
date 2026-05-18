/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.message;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEvidenceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorEvidenceJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageAttachmentJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageEvidenceRepository}.
 */
@Component
public class ConnectorMessageEvidenceRepositoryImpl implements ConnectorMessageEvidenceRepository {
    private final ConnectorEvidenceJpaRepository evidenceJpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;
    private final ConnectorMessageAttachmentJpaRepository attachmentJpaRepository;

    /**
     * Constructs an instance of ConnectorMessageEvidenceRepositoryImpl with the necessary JPA
     * repositories to perform persistence operations.
     *
     * @param evidenceJpaRepository   Repository for performing CRUD operations on
     *                                ConnectorMessageEvidenceEntity instances.
     * @param messageJpaRepository    Repository for performing CRUD operations on
     *                                ConnectorMessageEntity instances and finding messages by their
     *                                identifier.
     * @param attachmentJpaRepository Repository for performing CRUD operations on
     *                                ConnectorMessageAttachmentEntity instances and fetching
     *                                attachments by their identifier.
     */
    public ConnectorMessageEvidenceRepositoryImpl(
            ConnectorEvidenceJpaRepository evidenceJpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository,
            ConnectorMessageAttachmentJpaRepository attachmentJpaRepository) {
        this.evidenceJpaRepository = evidenceJpaRepository;
        this.messageJpaRepository = messageJpaRepository;
        this.attachmentJpaRepository = attachmentJpaRepository;
    }

    static ConnectorMessageEvidence toDomain(ConnectorMessageEvidenceEntity entity) {
        return ConnectorMessageEvidence
                .builder()
                .uuid(entity.getUuid())
                .attachment(ConnectorMessageAttachmentRepositoryImpl.toDomain(entity.getContent()))
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public ConnectorMessageEvidence save(
            @NonNull ConnectorMessageEvidence evidence,
            @NonNull String messageIdentifier) {
        var message = messageJpaRepository.findByIdentifier(messageIdentifier);
        var evidenceToSave = toEntity(evidence, message);
        var savedEvidence = evidenceJpaRepository.save(evidenceToSave);

        return toDomain(savedEvidence);
    }

    private ConnectorMessageEvidenceEntity toEntity(
            ConnectorMessageEvidence evidence,
            ConnectorMessageEntity message) {
        var content = this.attachmentJpaRepository.findByIdentifier(
                evidence.attachment().identifier());

        return ConnectorMessageEvidenceEntity
                .builder()
                .content(content)
                .type(evidence.type())
                .message(message)
                .build();
    }
}
