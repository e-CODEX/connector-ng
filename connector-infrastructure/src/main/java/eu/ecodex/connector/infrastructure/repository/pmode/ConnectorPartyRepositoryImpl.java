/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.repository.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.spi.pmode.ConnectorPartyRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorPartyEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorPartyJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.pmode.ConnectorProcessingModeJpaRepository;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorPartyRepository}.
 */
@Component
public class ConnectorPartyRepositoryImpl implements ConnectorPartyRepository {
    private final ConnectorPartyJpaRepository partyJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    public ConnectorPartyRepositoryImpl(
            ConnectorPartyJpaRepository partyJpaRepository,
            ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.partyJpaRepository = partyJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorParty} domain object into a {@link ConnectorPartyEntity} JPA
     * entity for database persistence.
     *
     * @param party          The {@link ConnectorParty} domain object to be converted. Must not be
     *                       null for successful conversion.
     * @param processingMode The {@link ConnectorProcessingModeEntity} representing the processing
     *                       mode of the party. Used in the creation of the resulting entity.
     *
     * @return A fully built {@link ConnectorPartyEntity} JPA entity containing relevant information
     *         from both the {@code party} and {@code processingMode}.
     */
    public static ConnectorPartyEntity toEntity(
            ConnectorParty party,
            ConnectorProcessingModeEntity processingMode) {
        return ConnectorPartyEntity
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
     * Converts a {@link ConnectorPartyEntity} JPA entity into a {@link ConnectorParty} domain
     * object. If the provided {@code entity} is {@code null}, the method returns {@code null}.
     *
     * @param entity The {@link ConnectorPartyEntity} instance to be converted. Can be {@code null},
     *               in which case {@code null} is returned.
     *
     * @return A fully built {@link ConnectorParty} domain object containing information extracted
     *         from the {@code entity}. Returns {@code null} if the {@code entity} is {@code null}.
     */
    public static ConnectorParty toDomain(ConnectorPartyEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorParty
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
    public List<ConnectorParty> saveAll(
            @NonNull List<ConnectorParty> parties,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
                businessDomainIdentifier.messageLaneIdentifier()
        );

        var savedParties = this.partyJpaRepository.saveAll(
                parties.stream().map(party -> toEntity(party, processingMode)).toList()
        );

        return savedParties.stream().map(ConnectorPartyRepositoryImpl::toDomain).toList();
    }

    // TODO to be removed
    @Override
    public ConnectorParty findByNameAndBusinessDomain(
            String name,
            ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ConnectorParty findByIdentifierAndRoleTypeAndBusinessDomain(
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
}
