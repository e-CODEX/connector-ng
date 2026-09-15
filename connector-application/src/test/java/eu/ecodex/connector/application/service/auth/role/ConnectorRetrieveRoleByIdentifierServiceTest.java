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
import eu.ecodex.connector.application.port.spi.auth.role.ConnectorRoleRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRetrieveRoleByIdentifierServiceTest {
    @Mock
    private ConnectorRoleRepository roleRepository;

    @InjectMocks
    private ConnectorRetrieveRoleByIdentifierService roleByIdentifierService;

    @Test
    void getById_should_return_role_found() {
        // Given
        var identifier = "uuid";
        var expected = ConnectorRole.builder()
            .uuid(identifier)
            .name("ROLE_NAME")
            .build();

        when(roleRepository.findByUuid(any())).thenReturn(Optional.of(expected));

        // When
        var found = roleByIdentifierService.execute(identifier);

        // Then
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(expected);
        verify(roleRepository).findByUuid(identifier);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void getById_should_throw_not_found_exception() {
        // Given
        var identifier = "uuid";
        when(roleRepository.findByUuid(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorRoleNotFoundException.class,
            () -> roleByIdentifierService.execute(identifier));

        // Then
        verify(roleRepository).findByUuid(identifier);
        verifyNoMoreInteractions(roleRepository);
    }
}
