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

import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import eu.ecodex.connector.domain.model.message.content.DetachedSignature;
import eu.ecodex.connector.domain.spi.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.content.ConnectorMessageBusinessContentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.content.ConnectorMessageBusinessDocumentDetachedSignatureEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.content.ConnectorMessageBusinessDocumentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageAttachmentJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.content.ConnectorMessageBusinessContentJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.content.ConnectorMessageBusinessDocumentJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.content.ConnectorMessageBusinessDocumentSignatureJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageBusinessContentRepository}.
 */
@Component
@SuppressWarnings("checkstyle:LineLength")
public class ConnectorMessageBusinessContentRepositoryImpl implements
        ConnectorMessageBusinessContentRepository {
    private final ConnectorMessageBusinessContentJpaRepository jpaRepository;
    private final ConnectorMessageJpaRepository messageJpaRepository;
    private final ConnectorMessageBusinessDocumentJpaRepository businessDocumentJpaRepository;
    private final ConnectorMessageAttachmentJpaRepository attachmentJpaRepository;
    private final ConnectorMessageBusinessDocumentSignatureJpaRepository detachedSignatureJpaRepository;

    /**
     * Constructs a new instance of ConnectorMessageBusinessContentRepositoryImpl.
     *
     * @param jpaRepository                  the repository for managing
     *                                       {@link ConnectorMessageBusinessContentEntity} entities
     * @param messageJpaRepository           the repository for managing
     *                                       {@link ConnectorMessageEntity} entities
     * @param businessDocumentJpaRepository  the repository for managing
     *                                       {@link ConnectorMessageBusinessDocumentEntity}
     *                                       entities
     * @param attachmentJpaRepository        the repository for managing
     *                                       {@link ConnectorMessageAttachmentEntity} entities
     * @param detachedSignatureJpaRepository the repository for managing
     *                                       {@link
     *                                       ConnectorMessageBusinessDocumentDetachedSignatureEntity}
     *                                       entities
     */
    public ConnectorMessageBusinessContentRepositoryImpl(
            ConnectorMessageBusinessContentJpaRepository jpaRepository,
            ConnectorMessageJpaRepository messageJpaRepository,
            ConnectorMessageBusinessDocumentJpaRepository businessDocumentJpaRepository,
            ConnectorMessageAttachmentJpaRepository attachmentJpaRepository,
            ConnectorMessageBusinessDocumentSignatureJpaRepository detachedSignatureJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.messageJpaRepository = messageJpaRepository;
        this.businessDocumentJpaRepository = businessDocumentJpaRepository;
        this.attachmentJpaRepository = attachmentJpaRepository;
        this.detachedSignatureJpaRepository = detachedSignatureJpaRepository;
    }

    static ConnectorMessageBusinessContent toDomain(
            ConnectorMessageBusinessContentEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessageBusinessContent
                .builder()
                .uuid(entity.getUuid())
                .xmlContent(ConnectorMessageAttachmentRepositoryImpl.toDomain(entity.getXmlContent()))
                .businessDocument(toDomain(entity.getBusinessDocument()))
                .build();
    }

    private static ConnectorMessageBusinessDocument toDomain(
            ConnectorMessageBusinessDocumentEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessageBusinessDocument
                .builder()
                .uuid(entity.getUuid())
                .aesType(entity.getAesType())
                .attachment(ConnectorMessageAttachmentRepositoryImpl.toDomain(entity.getAttachment()))
                .detachedSignature(toDomain(entity.getDetachedSignature()))
                .hashValue(entity.getHashValue())
                .build();
    }

    private static DetachedSignature toDomain(
            ConnectorMessageBusinessDocumentDetachedSignatureEntity entity) {
        if (entity == null) {
            return null;
        }

        return DetachedSignature
                .builder()
                .name(entity.getName())
                .signature(entity.getSignature())
                .mimeType(entity.getMimeType())
                .build();
    }

    @Override
    public ConnectorMessageBusinessContent save(
            @NonNull ConnectorMessageBusinessContent businessContent,
            @NonNull String messageIdentifier) {
        var message = this.messageJpaRepository.findByIdentifier(messageIdentifier);
        var contentToSave = toEntity(businessContent, message);
        var savedContent = this.jpaRepository.save(contentToSave);

        if (businessContent.businessDocument() != null) {
            var businessDocumentToSave = toEntity(businessContent.businessDocument(), savedContent);
            var savedBusinessDocument = this.businessDocumentJpaRepository.save(businessDocumentToSave);
            savedContent.setBusinessDocument(savedBusinessDocument);

            var documentDetachedSignature = businessContent.businessDocument().detachedSignature();

            if (documentDetachedSignature != null) {
                var savedDetachedSignature = this.detachedSignatureJpaRepository.save(
                        toEntity(documentDetachedSignature, savedBusinessDocument));
                savedBusinessDocument.setDetachedSignature(savedDetachedSignature);
            }
        }

        return toDomain(savedContent);
    }

    @Override
    public ConnectorMessageBusinessContent assignBusinessDocument(
            @NonNull String uuid,
            @NonNull ConnectorMessageBusinessDocument document) {
        var content = this.jpaRepository.findByUuid(uuid);
        var businessDocumentToSave = toEntity(document, content);

        var savedBusinessDocument = this.businessDocumentJpaRepository.save(businessDocumentToSave);

        var detachedSignature = document.detachedSignature();

        if (detachedSignature != null) {
            var savedDetachedSignature = this.detachedSignatureJpaRepository.save(
                    toEntity(detachedSignature, savedBusinessDocument)
            );
            savedBusinessDocument.setDetachedSignature(savedDetachedSignature);
        }

        content.setBusinessDocument(savedBusinessDocument);

        return toDomain(content);
    }

    private ConnectorMessageBusinessContentEntity toEntity(
            ConnectorMessageBusinessContent content, ConnectorMessageEntity message) {
        var xmlContent = this.attachmentJpaRepository.findByIdentifier(
                content.xmlContent().identifier());

        return ConnectorMessageBusinessContentEntity
                .builder()
                .xmlContent(xmlContent)
                .message(message)
                .build();
    }

    private ConnectorMessageBusinessDocumentEntity toEntity(
            ConnectorMessageBusinessDocument document,
            ConnectorMessageBusinessContentEntity content) {
        var attachment = this.attachmentJpaRepository.findByIdentifier(
                document.attachment().identifier());

        return ConnectorMessageBusinessDocumentEntity
                .builder()
                .aesType(document.aesType())
                .hashValue(document.hashValue())
                .businessContent(content)
                .attachment(attachment)
                .build();
    }

    private ConnectorMessageBusinessDocumentDetachedSignatureEntity toEntity(
            DetachedSignature detachedSignature, ConnectorMessageBusinessDocumentEntity document) {
        return ConnectorMessageBusinessDocumentDetachedSignatureEntity
                .builder()
                .signature(detachedSignature.signature())
                .name(detachedSignature.name())
                .mimeType(detachedSignature.mimeType())
                .businessDocument(document)
                .build();
    }
}
