/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.message;

import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAS4PropertiesRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAS4PropertiesEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageErrorEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEvidenceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.content.ConnectorMessageBusinessContentEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorBusinessDomainJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageAS4PropertiesJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.specification.MessageSpecification;
import eu.ecodex.connector.infrastructure.outbound.persistence.PaginationMapper;
import eu.ecodex.connector.infrastructure.outbound.persistence.pmode.ConnectorActionRepositoryImpl;
import eu.ecodex.connector.infrastructure.outbound.persistence.pmode.ConnectorPartyRepositoryImpl;
import eu.ecodex.connector.infrastructure.outbound.persistence.pmode.ConnectorServiceRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorMessageRepository}.
 */
@Component
public class ConnectorMessageRepositoryImpl implements ConnectorMessageRepository {
    private final ConnectorMessageJpaRepository messageJpaRepository;
    private final ConnectorMessageAS4PropertiesJpaRepository as4PropertiesJpaRepository;
    private final ConnectorBusinessDomainJpaRepository businessDomainJpaRepository;
    private final ConnectorMessageAS4PropertiesRepository as4PropertiesRepository;
    private final ConnectorMessageBusinessContentRepository messageBusinessContentRepository;
    private final PaginationMapper paginationMapper;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new {@code ConnectorMessageRepositoryImpl}.
     *
     * @param messageJpaRepository             JPA repository responsible for persisting and
     *                                         retrieving {@code ConnectorMessage} entities.
     * @param businessDomainJpaRepository      JPA repository responsible for managing * *
     *                                         {@code ConnectorBusinessDomain}.
     * @param as4PropertiesJpaRepository       JPA repository responsible for managing
     *                                         {@code ConnectorMessageAS4Properties} entities.
     * @param as4PropertiesRepository          JPA repository responsible for managing
     *                                         {@code ConnectorMessageAS4Properties} entities.
     * @param messageBusinessContentRepository JPA repository responsible for managing
     * @param paginationMapper                 Mapper for pagination.
     */
    public ConnectorMessageRepositoryImpl(
        ConnectorMessageJpaRepository messageJpaRepository,
        ConnectorBusinessDomainJpaRepository businessDomainJpaRepository,
        ConnectorMessageAS4PropertiesJpaRepository as4PropertiesJpaRepository,
        ConnectorMessageAS4PropertiesRepository as4PropertiesRepository,
        ConnectorMessageBusinessContentRepository messageBusinessContentRepository,
        PaginationMapper paginationMapper) {
        this.messageJpaRepository = messageJpaRepository;
        this.as4PropertiesJpaRepository = as4PropertiesJpaRepository;
        this.businessDomainJpaRepository = businessDomainJpaRepository;
        this.as4PropertiesRepository = as4PropertiesRepository;
        this.messageBusinessContentRepository = messageBusinessContentRepository;
        this.paginationMapper = paginationMapper;
    }

    private static ConnectorBusinessMessage.ConnectorBusinessMessageBuilder baseAttribute(
        ConnectorMessageEntity entity) {
        return ConnectorBusinessMessage
            .builder()
            .businessDomainIdentifier(
                ConnectorBusinessDomainIdentifier
                    .builder()
                    .messageLaneIdentifier(entity.getBusinessDomain().getIdentifier())
                    .build()
            )
            .identifier(entity.getIdentifier())
            .backendMessageIdentifier(entity.getBackendMessageIdentifier())
            .referenceToBackendMessageIdentifier(entity.getReferenceToBackendMessageIdentifier())
            .backendName(entity.getBackendName())
            .gatewayName(entity.getGatewayName())
            .direction(entity.getDirection())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .deletedAt(entity.getDeletedAt())
            .rejectedAt(entity.getRejectedAt())
            .confirmedAt(entity.getConfirmedAt())
            .deliveredToLinkPartnerAt(entity.getDeliveredToLinkPartnerAt())
            .as4Properties(toDomain(entity.getAs4Properties()));
    }

    private static ConnectorMessageAS4Properties toDomain(
        ConnectorMessageAS4PropertiesEntity entity) {
        return ConnectorMessageAS4Properties
            .builder()
            .referenceToIdentifier(entity.getReferenceToIdentifier())
            .conversationIdentifier(entity.getConversationIdentifier())
            .ebmsMessageIdentifier(entity.getEbmsMessageIdentifier())
            .originalSender(entity.getOriginalSender())
            .finalRecipient(entity.getFinalRecipient())
            .service(ConnectorServiceRepositoryImpl.toDomain(entity.getService()))
            .action(ConnectorActionRepositoryImpl.toDomain(entity.getAction()))
            .fromParty(ConnectorPartyRepositoryImpl.toDomain(entity.getFromParty()))
            .toParty(ConnectorPartyRepositoryImpl.toDomain(entity.getToParty()))
            .build();
    }

    private ConnectorBusinessMessage toDomain(ConnectorMessageEntity entity) {
        if (entity == null) {
            return null;
        }

        var businessContent = entity.getBusinessContent();
        var evidences = entity.getEvidences();
        var attachments = entity.getAttachments();
        var errors = entity.getErrors();

        return baseAttribute(entity)
            .businessContent(toBusinessContent(businessContent))
            .evidences(toEvidence(evidences))
            .attachments(toAttachment(attachments))
            .errors(toError(errors))
            .build();
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Override
    public ConnectorBusinessMessage save(@NonNull ConnectorBusinessMessage message) {
        var messageToSave = toEntity(message);
        var savedMessage = this.messageJpaRepository.save(messageToSave);

        this.as4PropertiesRepository.save(message);
        this.messageBusinessContentRepository.save(message.businessContent(), message.identifier());

        this.messageJpaRepository.flush();
        this.entityManager.refresh(savedMessage);

        return toDomain(savedMessage);
    }

    @Override
    public ConnectorBusinessMessage updateGatewayName(
        @NonNull String identifier,
        @NonNull String name) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);
        message.setGatewayName(name);
        var updated = this.messageJpaRepository.save(message);

        return toDomain(updated);
    }

    @Override
    public ConnectorBusinessMessage updateBackendName(
        @NonNull String identifier,
        @NonNull String name) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);
        message.setBackendName(name);
        var updated = this.messageJpaRepository.save(message);

        return toDomain(updated);
    }

    @Override
    public ConnectorBusinessMessage updateEbmsIdentifier(
        @NonNull String identifier,
        @NonNull String ebmsIdentifier) {
        var as4Properties = as4PropertiesJpaRepository.findByMessageIdentifier(identifier);
        as4Properties.setEbmsMessageIdentifier(ebmsIdentifier);
        var updated = as4PropertiesJpaRepository.save(as4Properties);

        return toDomain(updated.getMessage());
    }

    @Override
    public ConnectorBusinessMessage updateBackendIdentifier(
        @NonNull String identifier,
        @NonNull String backendIdentifier) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);
        message.setBackendMessageIdentifier(backendIdentifier);
        var updated = this.messageJpaRepository.save(message);

        return toDomain(updated);
    }

    @Override
    public ConnectorPageResult<ConnectorBusinessMessage> findAll(
        ConnectorPageRequest request,
        String identifier,
        String backendName,
        String businessDomainIdentifier,
        String service,
        String action) {
        var pageable = paginationMapper.toPageable(request);

        var specification = MessageSpecification.withFilters(
            identifier,
            backendName,
            businessDomainIdentifier,
            service,
            action
        );

        var messages = messageJpaRepository.findAll(specification, pageable).map(this::toDomain);

        return paginationMapper.toPageResult(messages);
    }

    @Override
    public List<ConnectorBusinessMessage> findAllByIdentifier(@NonNull List<String> identifiers) {
        return this.messageJpaRepository.findByIdentifierIn(identifiers)
                                        .stream()
                                        .map(this::toDomain)
                                        .toList();
    }

    @Override
    public ConnectorBusinessMessage findByIdentifier(@NonNull String identifier) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);

        return toDomain(message);
    }

    @Override
    public ConnectorBusinessMessage findByEbmsMessageIdentifierAndDirection(
        @NonNull String ebmsMessageIdentifier,
        @NonNull ConnectorMessageDirection direction) {
        var message = this.messageJpaRepository
            .findByAs4PropertiesEbmsMessageIdentifierAndDirection(
                ebmsMessageIdentifier, direction
            );

        return toDomain(message);
    }

    @Override
    public ConnectorBusinessMessage findByBackendMessageIdentifier(
        @NonNull String backendMessageIdentifier) {
        var message = this.messageJpaRepository.findByBackendMessageIdentifier(
            backendMessageIdentifier
        );

        return toDomain(message);
    }

    @Override
    public ConnectorBusinessMessage findReferencedBusinessMessage(
        String referenceToMessageIdentifier,
        ConnectorMessageDirection triggerDirection) {
        var invertedDirection = ConnectorMessageDirection.revert(triggerDirection);
        var relatedBusinessMessage = messageJpaRepository
            .findByAs4PropertiesEbmsMessageIdentifierAndDirection(
                referenceToMessageIdentifier,
                invertedDirection
            );

        if (relatedBusinessMessage == null) {
            relatedBusinessMessage = messageJpaRepository.findByBackendMessageIdentifier(
                referenceToMessageIdentifier);
        }

        if (relatedBusinessMessage == null) {
            relatedBusinessMessage = messageJpaRepository.findByIdentifier(
                referenceToMessageIdentifier);
        }

        return toDomain(relatedBusinessMessage);
    }

    @Override
    public List<ConnectorBusinessMessage> findByConversationIdentifier(
        @NonNull String conversationIdentifier) {
        var messages = this.messageJpaRepository.findByAs4PropertiesConversationIdentifier(
            conversationIdentifier
        );

        return messages.stream().map(this::toDomain).toList();
    }

    @Override
    public ConnectorBusinessMessage setAsRejected(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setRejectedAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    @Override
    public ConnectorBusinessMessage setAsConfirmed(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setConfirmedAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    @Override
    public ConnectorBusinessMessage setDeliveredToLinkPartnerAt(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setDeliveredToLinkPartnerAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    private ConnectorMessageEntity toEntity(ConnectorBusinessMessage message) {
        var businessDomain = this.businessDomainJpaRepository.findByIdentifier(
            message.businessDomainIdentifier().messageLaneIdentifier()
        );

        return ConnectorMessageEntity
            .builder()
            .businessDomain(businessDomain)
            .identifier(message.identifier())
            .backendMessageIdentifier(message.backendMessageIdentifier())
            .referenceToBackendMessageIdentifier(message.referenceToBackendMessageIdentifier())
            .backendName(message.backendName())
            .gatewayName(message.gatewayName())
            .direction(message.direction())
            .build();
    }

    private ConnectorMessageBusinessContent toBusinessContent(
        ConnectorMessageBusinessContentEntity businessContent) {
        return ConnectorMessageBusinessContentRepositoryImpl.toDomain(businessContent);
    }

    private List<ConnectorMessageAttachment> toAttachment(
        List<ConnectorMessageAttachmentEntity> attachments) {
        return attachments == null
               ? List.of()
               : attachments.stream()
                            .map(ConnectorMessageAttachmentRepositoryImpl::toDomain)
                            .toList();
    }

    private List<ConnectorMessageError> toError(List<ConnectorMessageErrorEntity> errors) {
        return errors == null
               ? List.of()
               : errors.stream()
                       .map(ConnectorMessageErrorRepositoryImpl::toDomain)
                       .toList();
    }

    private List<ConnectorMessageEvidence> toEvidence(
        Set<ConnectorMessageEvidenceEntity> evidences) {
        return evidences == null
               ? List.of()
               : evidences.stream()
                          .map(ConnectorMessageEvidenceRepositoryImpl::toDomain)
                          .toList();
    }
}
