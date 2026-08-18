/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorListUserServiceTest {

    @Mock
    ConnectorUserRepository repository;

    @InjectMocks
    ConnectorListUserService service;

    @Test
    void findAllWithRoles_should_return_roles_found() {
        // Given
        var expected = List.of(ConnectorUser.builder()
            .username("user")
            .uuid("identifier")
            .roles(Set.of(ConnectorRole.builder().name("ROLE_USER").build()))
            .build());
        when(repository.findAllWithRoles()).thenReturn(expected);

        // When
        var found = service.findAllWithRoles();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).usingRecursiveComparison().isEqualTo(expected);
        verify(repository).findAllWithRoles();
        verifyNoMoreInteractions(repository);
    }
}