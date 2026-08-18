/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.infrastructure.outbound.database.entity.user.ConnectorRoleEntity;
import eu.ecodex.connector.infrastructure.outbound.database.repository.auth.ConnectorRoleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRoleRepositoryImplTest {

    @Mock
    ConnectorRoleJpaRepository jpaRepository;

    @InjectMocks
    private ConnectorRoleRepositoryImpl repository;

    @Test
    void save_should_save_role_to_database() {
        // Given
        var uuid = "uuid";
        var name = "test";
        var role = ConnectorRole.builder().uuid(uuid).name(name).build();
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name(name).build();
        when(jpaRepository.findByUuid(any())).thenReturn(Optional.of(roleEntity));
        when(jpaRepository.save(any())).thenReturn(roleEntity);

        // When
        var saved = repository.save(role);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved).isEqualTo(role);
        verify(jpaRepository).findByUuid(uuid);
        verify(jpaRepository).save(roleEntity);

        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void findByUuid_should_find_role_by_uuid() {
        // Given
        var uuid = "uuid";
        var role = ConnectorRole.builder().uuid(uuid).name("test").build();
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name("test").build();
        when(jpaRepository.findByUuid(anyString())).thenReturn(Optional.of(roleEntity));

        // When
        var found = repository.findByUuid(uuid);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(role);
        verify(jpaRepository).findByUuid(uuid);

        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void findByName_should_find_role_by_name() {
        // Given
        var uuid = "uuid";
        var name = "test";
        var role = ConnectorRole.builder().uuid(uuid).name(name).build();
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name(name).build();
        when(jpaRepository.findByName(anyString())).thenReturn(Optional.of(roleEntity));

        // When
        var found = repository.findByName(name);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(role);
        verify(jpaRepository).findByName(name);

        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void findAll_should_find_all_roles() {
        // Given
        var uuid = "uuid";
        var name = "test";
        var role = ConnectorRole.builder().uuid(uuid).name(name).build();
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name(name).build();
        when(jpaRepository.findAll()).thenReturn(List.of(roleEntity));

        // When
        var found = repository.findAll();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).hasSize(1);
        assertThat(found.getFirst()).isEqualTo(role);
        verify(jpaRepository).findAll();

        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void deleteByUuid_should_delete_role() {
        // Given
        var uuid = "uuid";
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name("test").build();
        when(jpaRepository.findByUuid(anyString())).thenReturn(Optional.of(roleEntity));

        // When
        repository.deleteByUuid(uuid);

        // Then
        verify(jpaRepository).findByUuid(uuid);
        verify(jpaRepository).delete(roleEntity);

        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void findByNameIn_should_find_roles_by_name_in() {
        // Given
        var uuid = "uuid";
        var name = "test";
        var names = Set.of(name);
        var role = ConnectorRole.builder().uuid(uuid).name(name).build();
        var roleEntity = ConnectorRoleEntity.builder().uuid(uuid).name(name).build();
        when(jpaRepository.findByNameIn(anySet())).thenReturn(Set.of(roleEntity));

        // When
        var found = repository.findByNameIn(names);

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.stream().toList().getFirst()).isEqualTo(role);
        verify(jpaRepository).findByNameIn(names);

        verifyNoMoreInteractions(jpaRepository);
    }
}