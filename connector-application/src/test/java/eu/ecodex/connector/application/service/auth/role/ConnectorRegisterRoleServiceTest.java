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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorRoleAlreadyExistsException;
import eu.ecodex.connector.application.exception.ConnectorRoleBadRequestException;
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRegisterRoleServiceTest {
    @Mock
    private ConnectorRoleRepository roleRepository;

    @InjectMocks
    private ConnectorRegisterRoleService service;

    @Test
    void register_should_create_role() {
        // Given
        var newRole = "ROLE_ADMIN";
        var role = ConnectorRole.builder().name(newRole).build();

        when(roleRepository.save(any())).thenReturn(role);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // When
        var registered = service.execute(role);

        // Then
        assertNotNull(registered);
        verify(roleRepository).save(role);
        verify(roleRepository).findByName(newRole);

        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void register_should_not_create_role_when_uuid_is_set() {
        // Given
        var newRole = "ROLE_ADMIN";
        var role = ConnectorRole.builder().uuid("uuid").name(newRole).build();

        // When
        // Then
        assertThrows(ConnectorRoleBadRequestException.class, () -> service.execute(role));
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void register_should_not_create_role_when_already_exists() {
        // Given
        var newRole = "ROLE_ADMIN";
        var role = ConnectorRole.builder().name(newRole).build();
        var existingRole = ConnectorRole.builder()
            .uuid("uuid")
            .name(newRole).build();

        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(existingRole));

        // When
        assertThrows(ConnectorRoleAlreadyExistsException.class, () -> service.execute(role));

        // Then
        verify(roleRepository).findByName(newRole);
        verifyNoMoreInteractions(roleRepository);
    }
}
