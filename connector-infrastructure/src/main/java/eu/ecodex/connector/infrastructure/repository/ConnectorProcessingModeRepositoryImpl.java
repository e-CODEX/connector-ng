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
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeRepository;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorBusinessDomainJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorKeystoreJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.ConnectorProcessingModeJpaRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorProcessingModeRepository}.
 */
@Slf4j
@Component
public class ConnectorProcessingModeRepositoryImpl implements ConnectorProcessingModeRepository {
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;
    private final ConnectorBusinessDomainJpaRepository businessDomainJpaRepository;
    private final ConnectorKeystoreJpaRepository keystoreJpaRepository;

    /**
     * Constructs an instance of {@code ConnectorProcessingModeRepositoryImpl} with the specified
     * JPA repositories for processing mode, business domain, and keystore management.
     *
     * @param processingModeJpaRepository the repository for performing operations on
     *                                    {@code ConnectorProcessingModeEntity}.
     * @param businessDomainJpaRepository the repository for performing operations on
     *                                    {@code ConnectorBusinessDomainEntity}.
     * @param keystoreJpaRepository       the repository for performing operations on
     *                                    {@code ConnectorKeystoreEntity}.
     */
    public ConnectorProcessingModeRepositoryImpl(
            ConnectorProcessingModeJpaRepository processingModeJpaRepository,
            ConnectorBusinessDomainJpaRepository businessDomainJpaRepository,
            ConnectorKeystoreJpaRepository keystoreJpaRepository) {
        this.processingModeJpaRepository = processingModeJpaRepository;
        this.businessDomainJpaRepository = businessDomainJpaRepository;
        this.keystoreJpaRepository = keystoreJpaRepository;
    }

    @Override
    public ConnectorProcessingMode save(
            @NonNull ConnectorProcessingMode processingMode,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier
    ) {
        log.debug(
                "saving processing mode [{}] for business domain [{}]", processingMode,
                businessDomainIdentifier
        );

        var savedProcessingMode = this.processingModeJpaRepository.save(toEntity(processingMode));

        return toDomain(savedProcessingMode);
    }

    @Override
    public ConnectorProcessingMode updateKeystore(
            @NonNull String uuid, @NonNull String keystoreUuid) {
        log.debug("updating processing mode with uuid: [{}]", uuid);

        var existingProcessingMode = processingModeJpaRepository.findByUuid(uuid);

        var keystoreEntity = keystoreJpaRepository.findByUuid(keystoreUuid);

        existingProcessingMode.setTruststore(keystoreEntity);

        var updatedProcessingMode = processingModeJpaRepository.save(existingProcessingMode);

        return toDomain(updatedProcessingMode);
    }

    @Override
    public ConnectorProcessingMode findByUuid(@NonNull String uuid) {
        var processingMode = processingModeJpaRepository.findByUuid(uuid);

        return toDomain(processingMode);
    }

    @Override
    public ConnectorProcessingMode findByBusinessDomainIdentifier(
            @NonNull ConnectorBusinessDomainIdentifier identifier) {
        log.debug("finding processing mode for business domain [{}]", identifier);

        var processingMode = processingModeJpaRepository.findByBusinessDomainIdentifier(
                identifier.messageLaneIdentifier()
        );

        return toDomain(processingMode);
    }

    @Override
    public List<ConnectorProcessingMode> findAll() {
        return processingModeJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private ConnectorProcessingModeEntity toEntity(ConnectorProcessingMode processingMode) {
        var businessDomain = businessDomainJpaRepository.findByIdentifier(
                Objects.requireNonNull(processingMode.businessDomain())
                       .identifier().messageLaneIdentifier()
        );

        return ConnectorProcessingModeEntity
                .builder()
                .description(processingMode.description())
                .content(processingMode.content())
                .filename(processingMode.filename())
                .businessDomain(businessDomain)
                .build();
    }

    private ConnectorProcessingMode toDomain(ConnectorProcessingModeEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConnectorProcessingMode
                .builder()
                .businessDomain(
                        ConnectorBusinessDomainRepositoryImpl.toDomain(entity.getBusinessDomain()))
                .uuid(entity.getUuid())
                .description(entity.getDescription())
                .content(entity.getContent())
                .filename(entity.getFilename())
                .parties(entity.getParties().stream().map(
                        ConnectorPartyRepositoryImpl::toDomain).collect(
                        Collectors.toSet()))
                .actions(entity.getActions().stream().map(
                        ConnectorActionRepositoryImpl::toDomain).collect(
                        Collectors.toSet()))
                .services(entity.getServices().stream().map(
                        ConnectorServiceRepositoryImpl::toDomain).collect(
                        Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
