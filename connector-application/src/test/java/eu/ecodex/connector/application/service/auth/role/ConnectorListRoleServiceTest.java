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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorRoleName;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorListRoleServiceTest {

    @Mock
    ConnectorRoleRepository roleRepository;

    @InjectMocks
    ConnectorListRoleService service;

    @Test
    void findAll_should_return_all_roles() {
        // Given
        ConnectorRole roleAdmin =
            ConnectorRole.builder().name(ConnectorRoleName.ADMIN.name()).build();
        ConnectorRole roleUser =
            ConnectorRole.builder().name(ConnectorRoleName.USER.name()).build();

        when(roleRepository.findAll()).thenReturn(List.of(roleAdmin, roleUser));

        // When
        var allRoles = service.findAll();

        // Then
        assertThat(allRoles).hasSize(2);
        assertThat(allRoles).containsExactlyInAnyOrder(roleAdmin, roleUser);

        verifyNoMoreInteractions(roleRepository);

    }

}