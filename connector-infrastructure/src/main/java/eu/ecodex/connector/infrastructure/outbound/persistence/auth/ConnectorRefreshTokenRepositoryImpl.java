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


import eu.ecodex.connector.application.port.spi.auth.login.ConnectorRefreshTokenRepository;
import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRefreshTokenEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorRefreshTokenJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.persistence.user.ConnectorUserMapper;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConnectorRefreshTokenRepository}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorRefreshTokenRepositoryImpl implements ConnectorRefreshTokenRepository {

    ConnectorRefreshTokenJpaRepository jpaRepository;
    ConnectorUserJpaRepository userRepository;


    @Override
    public Optional<ConnectorRefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public ConnectorRefreshToken save(ConnectorRefreshToken refreshToken) {
        var user = userRepository.findByUuid(refreshToken.user().uuid())
                .orElseThrow();

        var saved = jpaRepository.save(toEntity(refreshToken, user));
        return toDomain(saved);
    }

    @Override
    public void delete(ConnectorRefreshToken refreshToken) {
        var user = userRepository.findByUuid(refreshToken.user().uuid())
                .orElseThrow();
        jpaRepository.delete(toEntity(refreshToken, user));
    }

    @Override
    public List<ConnectorRefreshToken> findByUserUuidAndRevoked(String uuid, boolean revoked) {
        return jpaRepository.findByUser_UuidAndRevoked(uuid, revoked).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public int revokeAllByUserUuid(String uuid) {
        return jpaRepository.revokeAllByUserUuid(uuid);
    }

    private ConnectorRefreshTokenEntity toEntity(ConnectorRefreshToken domain,
                                                 ConnectorUserEntity user) {

        return ConnectorRefreshTokenEntity.builder()
                .token(domain.uuid())
                .revoked(domain.revoked())
                .user(user)
                .expiresAt(domain.expiresAt())
                .build();
    }


    private ConnectorRefreshToken toDomain(ConnectorRefreshTokenEntity entity) {
        return ConnectorRefreshToken.builder()
                .uuid(entity.getToken())
                .user(ConnectorUserMapper.toDomain(entity.getUser()))
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}
