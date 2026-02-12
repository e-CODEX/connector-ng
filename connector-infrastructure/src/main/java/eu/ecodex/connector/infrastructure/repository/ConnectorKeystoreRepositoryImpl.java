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
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import eu.ecodex.connector.domain.spi.ConnectorKeystoreRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.entity.pmode.ConnectorKeystoreEntity;
import eu.ecodex.connector.infrastructure.outbound.persistence.entity.pmode.ConnectorProcessingModeEntity;
import eu.ecodex.connector.infrastructure.outbound.persistence.repository.ConnectorKeystoreJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.repository.ConnectorProcessingModeJpaRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of the {@link ConnectorKeystoreRepository}.
 */
@Slf4j
@Component
public class ConnectorKeystoreRepositoryImpl implements ConnectorKeystoreRepository {
    private final ConnectorKeystoreJpaRepository keystoreJpaRepository;
    private final ConnectorProcessingModeJpaRepository processingModeJpaRepository;

    public ConnectorKeystoreRepositoryImpl(
            ConnectorKeystoreJpaRepository keystoreJpaRepository,
            ConnectorProcessingModeJpaRepository processingModeJpaRepository) {
        this.keystoreJpaRepository = keystoreJpaRepository;
        this.processingModeJpaRepository = processingModeJpaRepository;
    }

    /**
     * Converts a {@link ConnectorKeystore} domain model and a {@link ConnectorProcessingModeEntity}
     * to a {@link ConnectorKeystoreEntity} database entity.
     *
     * @param keystore       the {@link ConnectorKeystore} to be converted.
     * @param processingMode the {@link ConnectorProcessingModeEntity} to associate with the
     *                       resulting entity.
     *
     * @return the corresponding {@link ConnectorKeystoreEntity}.
     */
    public static ConnectorKeystoreEntity toEntity(
            ConnectorKeystore keystore, ConnectorProcessingModeEntity processingMode) {
        return ConnectorKeystoreEntity
                .builder()
                .description(keystore.description())
                .content(keystore.content())
                .password(keystore.password())
                .type(keystore.type())
                .filename(keystore.filename())
                .processingMode(processingMode)
                .build();
    }

    /**
     * Converts a {@link ConnectorKeystoreEntity} instance to a {@link ConnectorKeystore} domain
     * model.
     *
     * @param entity the {@link ConnectorKeystoreEntity} to be converted; may be null.
     *
     * @return the corresponding {@link ConnectorKeystore} instance, or null if the input entity is
     *         null.
     */
    public static ConnectorKeystore toDomain(ConnectorKeystoreEntity entity) {
        // TODO handle null entity by returning null
        return ConnectorKeystore
                .builder()
                .uuid(entity.getUuid())
                .description(entity.getDescription())
                .content(entity.getContent())
                .password(entity.getPassword())
                .type(entity.getType())
                .filename(entity.getFilename())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public ConnectorKeystore save(
            @NonNull ConnectorKeystore keystore,
            @NonNull ConnectorBusinessDomainIdentifier businessDomainIdentifier) {
        log.debug(
                "saving keystore: [{}] for business domain [{}]",
                keystore, businessDomainIdentifier
        );

        var processingMode = this.processingModeJpaRepository.findByBusinessDomainIdentifier(
                businessDomainIdentifier.messageLaneIdentifier()
        );

        var savedKeystore = this.keystoreJpaRepository.save(toEntity(keystore, processingMode));

        return toDomain(savedKeystore);
    }
}
