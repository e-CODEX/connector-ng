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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAS4PropertiesEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorActionEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorPartyEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorServiceEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorActionJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorBusinessDomainJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorPartyJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorServiceJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageAS4PropertiesJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.message.ConnectorMessageJpaRepository;
import java.time.Instant;
import java.util.List;
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
    private final ConnectorServiceJpaRepository serviceJpaRepository;
    private final ConnectorActionJpaRepository actionJpaRepository;
    private final ConnectorPartyJpaRepository partyJpaRepository;

    /**
     * Creates a new {@code ConnectorMessageRepositoryImpl}.
     *
     * @param messageJpaRepository        JPA repository responsible for persisting and retrieving
     *                                    {@code ConnectorMessage} entities.
     * @param as4PropertiesJpaRepository  JPA repository responsible for managing
     *                                    {@code ConnectorMessageAS4Properties} entities.
     * @param businessDomainJpaRepository JPA repository responsible for managing
     *                                    {@code ConnectorBusinessDomain}.
     * @param serviceJpaRepository        JPA repository responsible for managing
     *                                    {@code ConnectorService} entities.
     * @param actionJpaRepository         JPA repository responsible for managing
     *                                    {@code ConnectorAction} entities.
     * @param partyJpaRepository          JPA repository responsible for managing
     *                                    {@code ConnectorParty} entities.
     */
    public ConnectorMessageRepositoryImpl(
            ConnectorMessageJpaRepository messageJpaRepository,
            ConnectorMessageAS4PropertiesJpaRepository as4PropertiesJpaRepository,
            ConnectorBusinessDomainJpaRepository businessDomainJpaRepository,
            ConnectorServiceJpaRepository serviceJpaRepository,
            ConnectorActionJpaRepository actionJpaRepository,
            ConnectorPartyJpaRepository partyJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
        this.as4PropertiesJpaRepository = as4PropertiesJpaRepository;
        this.businessDomainJpaRepository = businessDomainJpaRepository;
        this.serviceJpaRepository = serviceJpaRepository;
        this.actionJpaRepository = actionJpaRepository;
        this.partyJpaRepository = partyJpaRepository;
    }

    @Override
    public ConnectorMessage save(@NonNull ConnectorMessage message) {
        var messageToSave = toEntity(message);
        var savedMessage = this.messageJpaRepository.save(messageToSave);

        var as4Properties = message.as4Properties();
        var businessDomainIdentifier = message.businessDomainIdentifier().messageLaneIdentifier();
        var service = this.serviceJpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
                as4Properties.service().name(), businessDomainIdentifier
        );
        var action = this.actionJpaRepository.findByNameAndProcessingModeBusinessDomainIdentifier(
                as4Properties.action().name(), businessDomainIdentifier
        );
        var fromParty = this.partyJpaRepository
                .findByIdentifierAndRoleTypeAndProcessingModeBusinessDomainIdentifier(
                        as4Properties.fromParty().identifier(),
                        as4Properties.fromParty().roleType(),
                        businessDomainIdentifier
                );
        var toParty = this.partyJpaRepository
                .findByIdentifierAndRoleTypeAndProcessingModeBusinessDomainIdentifier(
                        as4Properties.toParty().identifier(),
                        as4Properties.toParty().roleType(),
                        businessDomainIdentifier
                );
        var as4PropertiesToSave = toEntity(
                as4Properties,
                savedMessage,
                service,
                action,
                fromParty,
                toParty
        );
        var savedAS4Properties = this.as4PropertiesJpaRepository.save(as4PropertiesToSave);
        savedMessage.setAs4Properties(savedAS4Properties);

        return toDomain(savedMessage);
    }

    @Override
    public ConnectorMessage updateGatewayName(@NonNull String identifier, @NonNull String name) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);
        message.setGatewayName(name);
        var updated = this.messageJpaRepository.save(message);

        return toDomain(updated);
    }

    @Override
    public ConnectorMessage updateBackendName(@NonNull String identifier, @NonNull String name) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);
        message.setBackendName(name);
        var updated = this.messageJpaRepository.save(message);

        return toDomain(updated);
    }

    @Override
    public ConnectorMessage updateEbmsIdentifier(
            @NonNull String identifier,
            @NonNull String ebmsIdentifier) {
        var as4Properties = as4PropertiesJpaRepository.findByMessageIdentifier(identifier);
        as4Properties.setEbmsMessageIdentifier(ebmsIdentifier);
        var updated = as4PropertiesJpaRepository.save(as4Properties);

        return toDomain(updated.getMessage());
    }

    @Override
    public ConnectorMessage findByIdentifier(String identifier) {
        var message = this.messageJpaRepository.findByIdentifier(identifier);

        return toDomain(message);
    }

    @Override
    public ConnectorMessage findByIdentifierAndDirection(
            ConnectorMessage message,
            ConnectorMessageDirection direction) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public List<ConnectorMessage> findByConversationIdentifier(
            @NonNull String conversationIdentifier) {
        var messages = this.messageJpaRepository.findByAs4PropertiesConversationIdentifier(
                conversationIdentifier
        );

        return messages.stream().map(this::toDomain).toList();
    }

    @Override
    public ConnectorMessage addEvidence(
            ConnectorMessage message, ConnectorMessageEvidence evidence) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorMessage setAsRejected(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setRejectedAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    @Override
    public ConnectorMessage setAsConfirmed(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setConfirmedAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    @Override
    public ConnectorMessage setDeliveredToGatewayAt(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setDeliveredToGatewayAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    @Override
    public ConnectorMessage setDeliveredToBackendAt(@NonNull String identifier) {
        var foundMessage = this.messageJpaRepository.findByIdentifier(identifier);
        foundMessage.setDeliveredToBackendAt(Instant.now());
        var updatedMessage = this.messageJpaRepository.save(foundMessage);

        return toDomain(updatedMessage);
    }

    private ConnectorMessageEntity toEntity(ConnectorMessage message) {
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

    private ConnectorMessageAS4PropertiesEntity toEntity(
            ConnectorMessageAS4Properties as4Properties,
            ConnectorMessageEntity message,
            ConnectorServiceEntity service,
            ConnectorActionEntity action,
            ConnectorPartyEntity fromParty,
            ConnectorPartyEntity toParty) {
        return ConnectorMessageAS4PropertiesEntity
                .builder()
                .referenceToIdentifier(as4Properties.referenceToIdentifier())
                .conversationIdentifier(as4Properties.conversationIdentifier())
                .ebmsMessageIdentifier(as4Properties.ebmsMessageIdentifier())
                .originalSender(as4Properties.originalSender())
                .finalRecipient(as4Properties.finalRecipient())
                .message(message)
                .service(service)
                .action(action)
                .fromParty(fromParty)
                .toParty(toParty)
                .build();
    }

    private ConnectorMessage toDomain(ConnectorMessageEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorMessage
                .builder()
                .businessDomainIdentifier(
                        ConnectorBusinessDomainIdentifier
                                .builder()
                                .messageLaneIdentifier(entity.getBusinessDomain().getIdentifier())
                                .build()
                )
                .identifier(entity.getIdentifier())
                .backendMessageIdentifier(entity.getBackendMessageIdentifier())
                .referenceToBackendMessageIdentifier(
                        entity.getReferenceToBackendMessageIdentifier())
                .backendName(entity.getBackendName())
                .gatewayName(entity.getGatewayName())
                .direction(entity.getDirection())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .rejectedAt(entity.getRejectedAt())
                .confirmedAt(entity.getConfirmedAt())
                .deliveredToBackendAt(entity.getDeliveredToBackendAt())
                .deliveredToGatewayAt(entity.getDeliveredToGatewayAt())
                .businessContent(
                        entity.getBusinessContent() == null
                                ? null
                                : ConnectorMessageBusinessContentRepositoryImpl.toDomain(
                                entity.getBusinessContent()
                        )
                )
                .as4Properties(toDomain(entity.getAs4Properties()))
                .evidences(
                        entity.getEvidences() == null
                                ? List.of()
                                : entity.getEvidences().stream()
                                        .map(ConnectorMessageEvidenceRepositoryImpl::toDomain)
                                        .toList()
                )
                .attachments(
                        entity.getAttachments() == null
                                ? List.of()
                                : entity.getAttachments().stream()
                                        .map(ConnectorMessageAttachmentRepositoryImpl::toDomain)
                                        .toList())
                .build();
    }

    private ConnectorMessageAS4Properties toDomain(ConnectorMessageAS4PropertiesEntity entity) {
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
}
