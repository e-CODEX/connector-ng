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
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEvidenceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorEvidenceJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageEvidenceRepository}.
 */
@Component
public class ConnectorMessageEvidenceRepositoryImpl implements ConnectorMessageEvidenceRepository {
    private final ConnectorEvidenceJpaRepository evidenceJpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;

    /**
     * Constructs an instance of ConnectorMessageEvidenceRepositoryImpl with the necessary JPA
     * repositories to perform persistence operations.
     *
     * @param evidenceJpaRepository Repository for performing CRUD operations on
     *                              ConnectorMessageEvidenceEntity instances.
     * @param messageJpaRepository  Repository for performing CRUD operations on
     *                              ConnectorMessageEntity instances and finding messages by their
     *                              identifier.
     */
    public ConnectorMessageEvidenceRepositoryImpl(
            ConnectorEvidenceJpaRepository evidenceJpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository) {
        this.evidenceJpaRepository = evidenceJpaRepository;
        this.messageJpaRepository = messageJpaRepository;
    }

    static ConnectorMessageEvidence toDomain(ConnectorMessageEvidenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessageEvidence
                .builder()
                .uuid(entity.getUuid())
                .content(entity.getContent().getBytes())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deliveredToLinkPartnerAt(entity.getDeliveredToLinkPartnerAt())
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

    @Override
    public ConnectorMessageEvidence findByUuid(String uuid) {
        var evidence = this.evidenceJpaRepository.findByUuid(uuid);
        return toDomain(evidence);
    }

    @Override
    public void setDeliveredToLinkPartnerAt(@NonNull String uuid) {
        var evidence = evidenceJpaRepository.findByUuid(uuid);
        evidence.setDeliveredToLinkPartnerAt(Instant.now());
        evidenceJpaRepository.save(evidence);
    }

    private ConnectorMessageEvidenceEntity toEntity(
            ConnectorMessageEvidence evidence,
            ConnectorMessageEntity message) {

        if (evidence.content() == null) {
            throw new IllegalArgumentException("Evidence content may not be null");
        }

        return ConnectorMessageEvidenceEntity
                .builder()
                .content(new String(evidence.content(), StandardCharsets.UTF_8))
                .type(evidence.type())
                .message(message)
                .build();
    }
}
