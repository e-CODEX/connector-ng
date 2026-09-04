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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.model.auth.ConnectorRefreshToken;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRefreshTokenEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorUserEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorRefreshTokenJpaRepository;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorUserJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRefreshTokenRepositoryImplTest {

    @Mock
    private ConnectorRefreshTokenJpaRepository jpaRepository;

    @Mock
    private ConnectorUserJpaRepository userJpaRepository;

    @InjectMocks
    private ConnectorRefreshTokenRepositoryImpl repository;

    private static ConnectorRefreshToken getExpectedRefreshToken() {
        var user = ConnectorUser.builder()
            .uuid("userId")
            .enabled(Boolean.FALSE)
            .roles(Set.of()).build();
        return ConnectorRefreshToken.builder().user(user).build();
    }

    private static ConnectorRefreshTokenEntity getRefreshTokenEntity() {
        var userEntity = ConnectorUserEntity.builder().uuid("userId").build();
        return ConnectorRefreshTokenEntity.builder()
            .user(userEntity).build();
    }

    @Test
    void deleteByUserUuid_should_delete_all_user_refresh_token() {
        // Given
        when(jpaRepository.deleteByUser_Uuid(any())).thenReturn(Integer.MIN_VALUE);

        // When
        var deleted = repository.deleteByUserUuid("userId");

        // Then
        assertThat(deleted).isEqualTo(Integer.MIN_VALUE);

        verify(jpaRepository).deleteByUser_Uuid("userId");
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void deleteByExpiryDateBefore_should_delete_all_refresh_tokens_before_a_specified_date() {
        // Given
        Instant now = Instant.now();
        when(jpaRepository.deleteByExpiresAtBefore(any())).thenReturn(Integer.MIN_VALUE);

        // When
        var deleted = repository.deleteByExpiryDateBefore(now);

        // Then
        assertThat(deleted).isEqualTo(Integer.MIN_VALUE);

        verify(jpaRepository).deleteByExpiresAtBefore(now);
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void deleteByRevokedAndExpiryDateBefore_should_deleted_ll_revoked_refresh_tokens_before_a_date() {
        // Given
        Instant now = Instant.now();
        when(jpaRepository.deleteByRevokedTrueAndExpiresAtBefore(any())).thenReturn(
            Integer.MIN_VALUE);

        // When
        var deleted = repository.deleteByRevokedAndExpiryDateBefore(now);

        // Then
        assertThat(deleted).isEqualTo(Integer.MIN_VALUE);

        verify(jpaRepository).deleteByRevokedTrueAndExpiresAtBefore(now);
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void findByToken_should_return_refresh_token() {
        // Given
        var expected = getExpectedRefreshToken();
        var expectedEntity = Optional.of(getRefreshTokenEntity());
        when(jpaRepository.findByToken(any())).thenReturn(expectedEntity);

        // When
        var found = repository.findByToken("token");

        // Then
        assertThat(found).isPresent().contains(expected);
        verify(jpaRepository).findByToken("token");
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void save_should_save_refresh_token() {
        // Given
        var token = getExpectedRefreshToken();
        var expectedEntity = getRefreshTokenEntity();

        when(userJpaRepository.findByUuid(any())).thenReturn(Optional.of(expectedEntity.getUser()));
        when(jpaRepository.save(any())).thenReturn(expectedEntity);

        // When
        var found = repository.save(token);

        // Then
        assertThat(found).isNotNull().isEqualTo(token);

        var captor = ArgumentCaptor.forClass(ConnectorRefreshTokenEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedEntity);

        verify(userJpaRepository).findByUuid(token.user().uuid());
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void delete_should_delete_a_refresh_token() {
        // Given
        var userId = "userId";
        var token = getExpectedRefreshToken();
        var expectedEntity = Optional.of(getRefreshTokenEntity());

        when(userJpaRepository.findByUuid(any())).thenReturn(
            Optional.of(expectedEntity.get().getUser()));
        doNothing().when(jpaRepository).delete(any());

        // When
        repository.delete(token);

        // Then
        var captor = ArgumentCaptor.forClass(ConnectorRefreshTokenEntity.class);
        verify(jpaRepository).delete(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedEntity.get());

        verify(userJpaRepository).findByUuid(userId);
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void findByUserUuidAndRevoked_should_return_found_refresh_tokens() {
        // Given
        var userId = "uuid";
        var token = getExpectedRefreshToken();
        var expectedEntity = List.of(getRefreshTokenEntity());

        when(jpaRepository.findByUser_UuidAndRevoked(any(), anyBoolean())).thenReturn(
            expectedEntity);

        // When
        var found = repository.findByUserUuidAndRevoked(userId, Boolean.TRUE);

        // Then
        assertThat(found).isNotEmpty().isEqualTo(List.of(token));
        verify(jpaRepository).findByUser_UuidAndRevoked(userId, Boolean.TRUE);
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

    @Test
    void revokeByUserUuid() {
        // Given
        var userId = "uuid";
        when(jpaRepository.revokeAllByUserUuid(any())).thenReturn(Integer.MIN_VALUE);

        // When
        var revoked = repository.revokeByUserUuid(userId);

        // Then
        assertThat(revoked).isEqualTo(Integer.MIN_VALUE);
        verify(jpaRepository).revokeAllByUserUuid(userId);
        verifyNoMoreInteractions(jpaRepository, userJpaRepository);
    }

}