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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRoleByName;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorAssignRoleServiceTest {
    @Mock
    private ConnectorRetrieveRoleByName retrieveRoleByName;

    @Mock
    private ConnectorUserRepository userRepository;

    @InjectMocks
    private ConnectorAssignRoleService assignRoleService;

    @Test
    void register_should_throw_exception_when_user_not_found() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";

        // When
        when(userRepository.findByUuid(userId)).thenReturn(Optional.empty());

        assertThrows(ConnectorUserNotFoundException.class,
            () -> assignRoleService.execute(userId, role));

        assertNoMoreInteractions();
    }

    @Test
    void register_should_throw_exception_when_role_not_found() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";
        var user = ConnectorUser.builder()
            .uuid(userId)
            .roles(Set.of(ConnectorRole.builder().name(role).build()))
            .build();

        // When
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(retrieveRoleByName.execute(role)).thenThrow(ConnectorRoleNotFoundException.class);

        // Then
        assertThrows(ConnectorRoleNotFoundException.class,
            () -> assignRoleService.execute(userId, role));

        assertNoMoreInteractions();
    }

    @Test
    void register_should_do_nothing_when_role_already_assigned() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";

        var roleAdmin = ConnectorRole.builder().name(role).build();
        var user = ConnectorUser.builder()
            .uuid(userId)
            .roles(new HashSet<>(Collections.singleton(roleAdmin)))
            .build();
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(retrieveRoleByName.execute(role)).thenReturn(roleAdmin);

        // When
        var resultUser = assignRoleService.execute(userId, role);

        // Then
        assertThat(resultUser).isNotNull();
        assertThat(resultUser).isEqualTo(user);
        verify(userRepository).findByUuid(userId);
        verify(retrieveRoleByName).execute(role);

        assertNoMoreInteractions();
    }

    @Test
    void register_should_add_role_to_user_when_user_has_no_role_assigned() {
        // Given
        var userId = "uuid";
        var roleName = "ROLE_USER";

        var roleUser = ConnectorRole.builder().name(roleName).build();
        var user = ConnectorUser.builder()
            .uuid(userId)
            .build();

        var expected = user.toBuilder()
            .roles(Set.of(roleUser))
            .build();
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(retrieveRoleByName.execute(roleName)).thenReturn(roleUser);
        when(userRepository.save(any())).thenReturn(expected);

        // When
        var resultUser = assignRoleService.execute(userId, roleName);

        // Then
        assertThat(resultUser).isNotNull();

        verify(userRepository).findByUuid(userId);
        verify(retrieveRoleByName).execute(roleName);
        verify(userRepository).save(expected);

        assertNoMoreInteractions();
    }


    @Test
    void register_should_add_role_to_user() {
        // Given
        var userId = "uuid";
        var roleName = "ROLE_USER";

        var roleUser = ConnectorRole.builder().name(roleName).build();
        var roleAdmin = ConnectorRole.builder().name("ROLE_ADMIN").build();
        var user = ConnectorUser.builder()
            .uuid(userId)
            .roles(new HashSet<>(Collections.singleton(roleAdmin)))
            .build();

        var expected = user.toBuilder()
            .roles(Set.of(roleAdmin, roleUser))
            .build();

        // When
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(retrieveRoleByName.execute(roleName)).thenReturn(roleUser);
        when(userRepository.save(any())).thenReturn(expected);

        var resultUser = assignRoleService.execute(userId, roleName);

        // Then
        assertThat(resultUser).isNotNull();
        verify(userRepository).findByUuid(userId);
        verify(retrieveRoleByName).execute(roleName);
        verify(userRepository).save(expected);

        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(retrieveRoleByName, userRepository);
    }
}
