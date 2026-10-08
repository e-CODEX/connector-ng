/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.role;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.role.ConnectorRoleInUseException;
import eu.ecodex.connector.application.exception.role.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRemoveRoleServiceTest {
    @Mock
    private ConnectorRoleRepository repository;
    @Mock
    private ConnectorUserRepository userRepository;

    @InjectMocks
    private ConnectorRemoveRoleService service;

    @Test
    void delete_by_identifier_should_delete_role() {
        // Given
        var uuid = "uuid";
        var connectorRole = ConnectorRole.builder()
            .uuid(uuid)
            .build();

        when(repository.findByUuid(any())).thenReturn(Optional.of(connectorRole));
        when(userRepository.existsByRolesUuid(any())).thenReturn(Boolean.FALSE);
        doNothing().when(repository).deleteByUuid(any());

        // When
        service.execute(uuid);

        // Then
        verify(repository).findByUuid(uuid);
        verify(userRepository).existsByRolesUuid(uuid);
        verify(repository).deleteByUuid(uuid);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_by_identifier_should_not_delete_role_when_role_is_in_use() {
        // Given
        var uuid = "uuid";
        var connectorRole = ConnectorRole.builder()
            .uuid(uuid)
            .build();

        when(repository.findByUuid(any())).thenReturn(Optional.of(connectorRole));
        when(userRepository.existsByRolesUuid(any())).thenReturn(Boolean.TRUE);

        // When
        assertThrows(ConnectorRoleInUseException.class, () -> service.execute(uuid));

        // Then
        verify(repository).findByUuid(uuid);
        verify(userRepository).existsByRolesUuid(uuid);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_by_identifier_should_not_delete_role_when_role_not_found() {
        // Given
        var uuid = "uuid";

        when(repository.findByUuid(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorRoleNotFoundException.class, () -> service.execute(uuid));

        // Then
        verify(repository).findByUuid(uuid);
        verifyNoMoreInteractions(repository);
    }
}
