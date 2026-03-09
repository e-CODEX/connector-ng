/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEvidenceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorEvidenceJpaRepository;
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

    public ConnectorMessageEvidenceRepositoryImpl(
            ConnectorEvidenceJpaRepository evidenceJpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository) {
        this.evidenceJpaRepository = evidenceJpaRepository;
        this.messageJpaRepository = messageJpaRepository;
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

    static ConnectorMessageEvidence toDomain(ConnectorMessageEvidenceEntity entity) {
        return ConnectorMessageEvidence
                .builder()
                .uuid(entity.getUuid())
                .content(entity.getContent())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ConnectorMessageEvidenceEntity toEntity(
            ConnectorMessageEvidence evidence, ConnectorMessageEntity message) {
        return ConnectorMessageEvidenceEntity
                .builder()
                .content(evidence.content())
                .type(evidence.type())
                .message(message)
                .build();
    }
}
