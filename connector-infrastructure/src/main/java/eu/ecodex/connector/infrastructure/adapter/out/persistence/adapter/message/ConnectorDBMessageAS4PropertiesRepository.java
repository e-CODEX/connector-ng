/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.message;

import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAS4PropertiesRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.pmode.ConnectorDBProcessingModeActionRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.pmode.ConnectorDBProcessingModePartyRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.pmode.ConnectorDBProcessingModeServiceRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.message.ConnectorMessageAS4PropertiesEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.message.ConnectorMessageEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeActionEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModePartyEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeServiceEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.message.ConnectorMessageAS4PropertiesJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.message.ConnectorMessageJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModeActionJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModePartyJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModeServiceJpaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

/**
 * RDBMS implementation of {@link ConnectorMessageAS4PropertiesRepository}.
 */
@Component
public class ConnectorDBMessageAS4PropertiesRepository
    implements ConnectorMessageAS4PropertiesRepository {
    private final ConnectorMessageJpaRepository messageJpaRepository;
    private final ConnectorMessageAS4PropertiesJpaRepository as4PropertiesJpaRepository;
    private final ConnectorProcessingModeServiceJpaRepository serviceJpaRepository;
    private final ConnectorProcessingModeActionJpaRepository actionJpaRepository;
    private final ConnectorProcessingModePartyJpaRepository partyJpaRepository;

    /**
     * Constructs a new instance of the {@link ConnectorDBMessageAS4PropertiesRepository} class.
     *
     * @param messageJpaRepository       The repository for managing connector messages.
     * @param as4PropertiesJpaRepository The repository for managing AS4 properties associated with
     *                                   connector messages.
     * @param serviceJpaRepository       The repository for managing connector services.
     * @param actionJpaRepository        The repository for managing connector actions.
     * @param partyJpaRepository         The repository for managing connector parties.
     */
    public ConnectorDBMessageAS4PropertiesRepository(
        ConnectorMessageJpaRepository messageJpaRepository,
        ConnectorMessageAS4PropertiesJpaRepository as4PropertiesJpaRepository,
        ConnectorProcessingModeServiceJpaRepository serviceJpaRepository,
        ConnectorProcessingModeActionJpaRepository actionJpaRepository,
        ConnectorProcessingModePartyJpaRepository partyJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
        this.as4PropertiesJpaRepository = as4PropertiesJpaRepository;
        this.serviceJpaRepository = serviceJpaRepository;
        this.actionJpaRepository = actionJpaRepository;
        this.partyJpaRepository = partyJpaRepository;
    }

    @Override
    public ConnectorMessageAS4Properties save(@NonNull ConnectorBusinessMessage message) {
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

        var savedMessage = this.messageJpaRepository.findByIdentifier(message.identifier());

        var as4PropertiesToSave = toEntity(
            as4Properties,
            savedMessage,
            service,
            action,
            fromParty,
            toParty
        );

        var savedAS4Properties = this.as4PropertiesJpaRepository.save(as4PropertiesToSave);

        return toDomain(savedAS4Properties);
    }

    private ConnectorMessageAS4PropertiesEntity toEntity(
        ConnectorMessageAS4Properties as4Properties,
        ConnectorMessageEntity message,
        ConnectorProcessingModeServiceEntity service,
        ConnectorProcessingModeActionEntity action,
        ConnectorProcessingModePartyEntity fromParty,
        ConnectorProcessingModePartyEntity toParty) {
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

    private ConnectorMessageAS4Properties toDomain(
        ConnectorMessageAS4PropertiesEntity entity) {
        return ConnectorMessageAS4Properties
            .builder()
            .referenceToIdentifier(entity.getReferenceToIdentifier())
            .conversationIdentifier(entity.getConversationIdentifier())
            .ebmsMessageIdentifier(entity.getEbmsMessageIdentifier())
            .originalSender(entity.getOriginalSender())
            .finalRecipient(entity.getFinalRecipient())
            .service(ConnectorDBProcessingModeServiceRepository.toDomain(entity.getService()))
            .action(ConnectorDBProcessingModeActionRepository.toDomain(entity.getAction()))
            .fromParty(ConnectorDBProcessingModePartyRepository.toDomain(entity.getFromParty()))
            .toParty(ConnectorDBProcessingModePartyRepository.toDomain(entity.getToParty()))
            .build();
    }
}
