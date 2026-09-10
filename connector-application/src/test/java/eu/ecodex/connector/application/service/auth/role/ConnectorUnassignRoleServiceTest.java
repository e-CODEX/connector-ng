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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorRoleNotFoundException;
import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.role.ConnectorRetrieveRoleByName;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorUnassignRoleServiceTest {
    @Mock
    ConnectorRetrieveRoleByName retrieveRoleByName;

    @Mock
    ConnectorUserRepository userRepository;

    @InjectMocks
    ConnectorUnassignRoleService unassignRoleService;

    @Test
    void remove_should_throw_exception_when_user_not_found() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";

        // When
        when(userRepository.findByUuid(userId)).thenReturn(Optional.empty());

        // Then
        assertThrows(ConnectorUserNotFoundException.class,
            () -> unassignRoleService.execute(userId, role));

        assertNoMoreInteractions();
    }

    @Test
    void remove_should_throw_exception_when_role_not_found() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";

        // When
        var user = ConnectorUser.builder()
            .uuid(userId)
            .roles(Set.of(ConnectorRole.builder().name(role).build()))
            .build();

        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(retrieveRoleByName.execute(role)).thenThrow(ConnectorRoleNotFoundException.class);

        // Then
        assertThrows(ConnectorRoleNotFoundException.class,
            () -> unassignRoleService.execute(userId, role));

        assertNoMoreInteractions();
    }

    @Test
    void remove_should_do_nothing_when_user_has_no_role() {
        // Given
        var userId = "uuid";
        var role = "ROLE_ADMIN";
        var user = ConnectorUser.builder()
            .uuid(userId)
            .build();

        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));

        // When
        var resultUser = unassignRoleService.execute(userId, role);

        // Then
        assertThat(resultUser).isNotNull();
        assertThat(resultUser).isEqualTo(user);
        verify(userRepository).findByUuid(userId);
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(retrieveRoleByName, userRepository);
    }
}
