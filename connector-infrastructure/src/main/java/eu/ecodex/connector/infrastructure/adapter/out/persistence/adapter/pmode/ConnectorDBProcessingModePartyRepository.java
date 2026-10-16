/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter.pmode;

import eu.ecodex.connector.application.port.spi.pmode.ConnectorProcessingModePartyRepository;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeParty;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModePartyEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModeJpaRepository;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.pmode.ConnectorProcessingModePartyJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * RDBMS implementation of {@link ConnectorProcessingModePartyRepository}.
 */
@Component
public class ConnectorDBProcessingModePartyRepository
    implements ConnectorProcessingModePartyRepository {
    private final ConnectorProcessingModePartyJpaRepository partyJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    public ConnectorDBProcessingModePartyRepository(
        ConnectorProcessingModePartyJpaRepository partyJpaRepository,
        ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.partyJpaRepository = partyJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorProcessingModeParty} domain object into a
     * {@link ConnectorProcessingModePartyEntity} JPA entity for database persistence.
     *
     * @param party          The {@link ConnectorProcessingModeParty} domain object to be converted.
     *                       Must not be null for successful conversion.
     * @param processingMode The {@link ConnectorProcessingModeEntity} representing the processing
     *                       mode of the party. Used in the creation of the resulting entity.
     *
     * @return A fully built {@link ConnectorProcessingModePartyEntity} JPA entity containing
     *     relevant information from both the {@code party} and {@code processingMode}.
     */
    public static ConnectorProcessingModePartyEntity toEntity(
        ConnectorProcessingModeParty party,
        ConnectorProcessingModeEntity processingMode) {
        return ConnectorProcessingModePartyEntity
            .builder()
            .name(party.name())
            .identifier(party.identifier())
            .identifierType(party.identifierType())
            .role(party.role())
            .roleType(party.roleType())
            .isHome(party.isHome())
            .processingMode(processingMode)
            .build();
    }

    /**
     * Converts a {@link ConnectorProcessingModePartyEntity} JPA entity into a
     * {@link ConnectorProcessingModeParty} domain object. If the provided {@code entity} is
     * {@code null}, the method returns {@code null}.
     *
     * @param entity The {@link ConnectorProcessingModePartyEntity} instance to be converted. Can be
     *               {@code null}, in which case {@code null} is returned.
     *
     * @return A fully built {@link ConnectorProcessingModeParty} domain object containing
     *     information extracted from the {@code entity}. Returns {@code null} if the {@code entity}
     *     is {@code null}.
     */
    public static ConnectorProcessingModeParty toDomain(ConnectorProcessingModePartyEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorProcessingModeParty
            .builder()
            .name(entity.getName())
            .identifier(entity.getIdentifier())
            .identifierType(entity.getIdentifierType())
            .role(entity.getRole())
            .roleType(entity.getRoleType())
            .isHome(entity.isHome())
            .build();
    }

    @Override
    public List<ConnectorProcessingModeParty> saveAll(
        @NonNull List<ConnectorProcessingModeParty> parties,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
            businessDomainIdentifier.messageLaneIdentifier()
        );

        var savedParties = this.partyJpaRepository.saveAll(
            parties.stream().map(party -> toEntity(party, processingMode)).toList()
        );

        return savedParties.stream()
                           .map(ConnectorDBProcessingModePartyRepository::toDomain)
                           .toList();
    }

    @Override
    public ConnectorProcessingModeParty findByIdentifierAndRoleTypeAndBusinessDomain(
        @NonNull String identifier,
        @NonNull ConnectorPartyRoleType roleType,
        @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var party = this.partyJpaRepository
            .findByIdentifierAndRoleTypeAndProcessingModeBusinessDomainIdentifier(
                identifier,
                roleType,
                businessDomainIdentifier.messageLaneIdentifier()
            );

        return toDomain(party);
    }

    @Override
    public List<ConnectorProcessingModeParty> findAllByBusinessDomainIdentifier(
        @NonNull ConnectorBusinessDomainIdentifier identifier) {
        var parties = partyJpaRepository.findByProcessingModeBusinessDomainIdentifier(
            identifier.messageLaneIdentifier(),
            Sort.by(Sort.Order.asc("identifier"))
        );

        return parties.stream().map(ConnectorDBProcessingModePartyRepository::toDomain).toList();
    }
}
