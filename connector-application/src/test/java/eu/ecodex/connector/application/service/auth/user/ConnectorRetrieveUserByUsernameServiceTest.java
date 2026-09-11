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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorRetrieveUserByUsernameServiceTest {
    @InjectMocks
    ConnectorRetrieveUserByUsernameService retrieveUserByUsernameService;
    @Mock
    private ConnectorUserRepository repository;

    @Test
    void getByUsername_should_return_found_user() {
        // Given
        var identifier = "uuid";
        var username = "user";
        var expected = ConnectorUser.builder()
            .uuid(identifier)
            .username(username)
            .build();
        when(repository.findByUsername(any())).thenReturn(Optional.of(expected));

        // When
        var found = retrieveUserByUsernameService.execute(username);

        // Then
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(expected);
        verify(repository).findByUsername(username);
        assertNoMoreInteractions();
    }

    @Test
    void getByUsername_should_throw_not_found_exception() {
        // Given
        var username = "user";
        when(repository.findByUsername(any())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserNotFoundException.class,
            () -> retrieveUserByUsernameService.execute(username));

        // Then
        verify(repository).findByUsername(username);
        assertNoMoreInteractions();
    }


    @Test
    void getActiveUserByUsername_should_return_found_user() {
        // Given
        var identifier = "uuid";
        var username = "user";
        var email = "myEmail@test.com";
        var expected = ConnectorUser.builder()
            .uuid(identifier)
            .username(username)
            .email(email)
            .enabled(true)
            .build();
        when(repository.findByUsernameAndActive(any(), anyBoolean())).thenReturn(
            Optional.of(expected));

        // When
        var found = retrieveUserByUsernameService.execute(username, Boolean.TRUE);

        // Then
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(expected);
        verify(repository).findByUsernameAndActive(username, Boolean.TRUE);
        assertNoMoreInteractions();
    }

    @Test
    void getActiveUserByUsername_should_return_not_found_when_user_not_active() {
        //Given
        var username = "user";
        when(repository.findByUsernameAndActive(any(), anyBoolean())).thenReturn(Optional.empty());

        // When
        assertThrows(ConnectorUserNotFoundException.class,
            () -> retrieveUserByUsernameService.execute(username, Boolean.FALSE));

        // Then
        verify(repository).findByUsernameAndActive(username, Boolean.FALSE);
        assertNoMoreInteractions();
    }

    private void assertNoMoreInteractions() {
        verifyNoMoreInteractions(repository);
    }
}