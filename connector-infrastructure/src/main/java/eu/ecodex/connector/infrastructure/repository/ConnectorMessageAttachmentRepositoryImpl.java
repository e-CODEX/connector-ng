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

import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageAttachmentJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageAttachmentRepository}.
 */
@Component
public class ConnectorMessageAttachmentRepositoryImpl implements
        ConnectorMessageAttachmentRepository {
    private final ConnectorMessageAttachmentJpaRepository jpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;

    public ConnectorMessageAttachmentRepositoryImpl(
            ConnectorMessageAttachmentJpaRepository jpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.messageJpaRepository = messageJpaRepository;
    }

    @Override
    public ConnectorMessageAttachment save(ConnectorMessageAttachment attachment) {
        var attachmentToSave = toEntity(attachment);
        var savedAttachment = jpaRepository.save(attachmentToSave);

        return toDomain(savedAttachment);
    }

    @Override
    public ConnectorMessageAttachment findByIdentifier(@NonNull String identifier) {
        var attachment = this.jpaRepository.findByIdentifier(identifier);

        return toDomain(attachment);
    }

    @Override
    public ConnectorPageResult<ConnectorMessageAttachment> findAll(ConnectorPageRequest request) {
        var pageable = PageRequest.of(request.page(), request.size());

        var result = jpaRepository.findAll(pageable);

        return new ConnectorPageResult<>(
                result.getContent().stream().map(this::toDomain).toList(),
                result.getTotalElements(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Override
    public void attachToMessage(
            @NonNull String attachmentIdentifier,
            @NonNull String messageIdentifier) {
        var attachment = jpaRepository.findByIdentifier(attachmentIdentifier);

        if (attachment.getMessage() != null) {
            throw new IllegalStateException("attachment already assigned to a message");
        }

        var message = messageJpaRepository.findByIdentifier(messageIdentifier);
        attachment.setMessage(message);
        jpaRepository.save(attachment);
    }

    private ConnectorMessageAttachmentEntity toEntity(ConnectorMessageAttachment attachment) {
        return ConnectorMessageAttachmentEntity
                .builder()
                .identifier(attachment.identifier())
                .name(attachment.name())
                .size(attachment.size())
                .contentType(attachment.contentType())
                .description(attachment.description())
                .storage(attachment.storage())
                .build();
    }

    private ConnectorMessageAttachment toDomain(ConnectorMessageAttachmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessageAttachment
                .builder()
                .identifier(entity.getIdentifier())
                .name(entity.getName())
                .size(entity.getSize())
                .contentType(entity.getContentType())
                .description(entity.getDescription())
                .storage(entity.getStorage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
