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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.user.ConnectorLastAdministratorException;
import eu.ecodex.connector.application.exception.user.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRemoveUserServiceTest {
    @Mock
    private ConnectorUserRepository repository;
    @Mock
    private ConnectorAdminSafeguard adminSafeguard;

    @InjectMocks
    private ConnectorRemoveUserService service;

    @Test
    void execute_should_delete_user() {
        // Given
        var identifier = "uuid";
        var user = ConnectorUser.defaultAdminUser().toBuilder().uuid(identifier).build();
        when(repository.findByUuid(identifier)).thenReturn(Optional.of(user));

        // When
        service.execute(identifier);

        // Then
        var order = inOrder(adminSafeguard, repository);
        order.verify(adminSafeguard).assertAnotherAdministratorRemains(user);
        order.verify(repository).deleteByUuid(identifier);
    }

    @Test
    void execute_should_not_delete_the_last_administrator() {
        // Given
        var identifier = "uuid";
        var user = ConnectorUser.defaultAdminUser().toBuilder().uuid(identifier).build();
        when(repository.findByUuid(identifier)).thenReturn(Optional.of(user));
        doThrow(new ConnectorLastAdministratorException("last"))
            .when(adminSafeguard).assertAnotherAdministratorRemains(user);

        // When
        assertThatThrownBy(() -> service.execute(identifier))
            .isInstanceOf(ConnectorLastAdministratorException.class);

        // Then
        verify(repository, never()).deleteByUuid(any());
    }

    @Test
    void execute_should_throw_not_found_when_the_user_does_not_exist() {
        // Given
        when(repository.findByUuid("unknown")).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> service.execute("unknown"))
            .isInstanceOf(ConnectorUserNotFoundException.class);

        // Then
        verifyNoInteractions(adminSafeguard);
        verify(repository, never()).deleteByUuid(any());
    }
}
