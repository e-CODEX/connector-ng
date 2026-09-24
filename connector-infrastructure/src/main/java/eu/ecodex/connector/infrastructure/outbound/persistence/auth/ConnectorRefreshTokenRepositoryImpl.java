/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.auth;


import eu.ecodex.connector.application.port.spi.auth.refreshtoken.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRefreshTokenEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserRefreshTokenJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.user.ConnectorUserMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRefreshTokenRepository}.
 */
@Slf4j
@Service
public class ConnectorRefreshTokenRepositoryImpl implements ConnectorRefreshTokenRepository {
    private final ConnectorUserRefreshTokenJpaRepository jpaRepository;
    private final ConnectorUserJpaRepository userRepository;

    public ConnectorRefreshTokenRepositoryImpl(ConnectorUserRefreshTokenJpaRepository jpaRepository,
                                               ConnectorUserJpaRepository userRepository) {
        this.jpaRepository = jpaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ConnectorRefreshToken> findByToken(@NonNull String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public ConnectorRefreshToken save(@NonNull ConnectorRefreshToken refreshToken) {
        var user = userRepository.findByUuid(refreshToken.user().uuid()).orElseThrow();
        var saved = jpaRepository.save(toEntity(refreshToken, user));
        return toDomain(saved);
    }

    @Override
    public void delete(@NonNull ConnectorRefreshToken refreshToken) {
        var user = userRepository.findByUuid(refreshToken.user().uuid()).orElseThrow();
        jpaRepository.delete(toEntity(refreshToken, user));
    }

    @Override
    public List<ConnectorRefreshToken> findByUserUuidAndRevoked(@NonNull String uuid,
                                                                boolean revoked) {
        return jpaRepository.findByUser_UuidAndRevoked(uuid, revoked).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public int revokeByUserUuid(@NonNull String uuid) {
        return jpaRepository.revokeAllByUserUuid(uuid);
    }

    @Override
    public int deleteByUserUuid(@NonNull String uuid) {
        return jpaRepository.deleteByUser_Uuid(uuid);
    }

    @Override
    public int deleteByExpiryDateBefore(@NonNull Instant expiryDate) {
        return jpaRepository.deleteByExpiresAtBefore(expiryDate);
    }

    @Override
    public int deleteByRevokedAndExpiryDateBefore(@NonNull Instant expiryDate) {
        return jpaRepository.deleteByRevokedTrueAndExpiresAtBefore(expiryDate);
    }

    /**
     * Converts a {@link ConnectorRefreshToken} domain object into a
     * {@link ConnectorRefreshTokenEntity}.
     *
     * @param domain The {@link ConnectorRefreshToken} instance to be converted.
     *               This represents the domain-level refresh token.
     * @param user   The {@link ConnectorUserEntity} instance associated with the
     *               provided refresh token. This represents the entity-level user.
     *
     * @return A new {@link ConnectorRefreshTokenEntity} built from the provided
     *     {@link ConnectorRefreshToken} and {@link ConnectorUserEntity}.
     */
    private ConnectorRefreshTokenEntity toEntity(@NonNull ConnectorRefreshToken domain,
                                                 ConnectorUserEntity user) {
        return ConnectorRefreshTokenEntity.builder()
            .token(domain.token())
            .revoked(domain.revoked())
            .user(user)
            .expiresAt(domain.expiresAt())
            .build();
    }

    /**
     * Converts a {@link ConnectorRefreshTokenEntity} entity instance to its corresponding
     * domain model {@link ConnectorRefreshToken}.
     *
     * @param entity The {@link ConnectorRefreshTokenEntity} instance to be converted.
     *               This represents the persistence-level representation of a refresh token.
     *
     * @return A {@link ConnectorRefreshToken} instance constructed from the provided entity.
     */
    private ConnectorRefreshToken toDomain(@NonNull ConnectorRefreshTokenEntity entity) {
        return ConnectorRefreshToken.builder()
            .token(entity.getToken())
            .user(ConnectorUserMapper.toDomain(entity.getUser()))
            .revoked(entity.isRevoked())
            .createdAt(entity.getCreatedAt())
            .expiresAt(entity.getExpiresAt())
            .build();
    }
}
