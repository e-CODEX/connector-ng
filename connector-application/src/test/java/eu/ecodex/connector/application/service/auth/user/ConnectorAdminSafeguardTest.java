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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.application.exception.user.ConnectorLastAdministratorException;
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
class ConnectorAdminSafeguardTest {
    @Mock
    private ConnectorUserRepository repository;

    @InjectMocks
    private ConnectorAdminSafeguard safeguard;

    @Test
    void should_refuse_the_only_enabled_administrator() {
        var admin = administrator("admin-1");
        when(repository.findEnabledByRoleNameForUpdate(ConnectorRole.DEFAULT_ADMIN_ROLE))
            .thenReturn(List.of(admin));

        assertThatThrownBy(() -> safeguard.assertAnotherAdministratorRemains(admin))
            .isInstanceOf(ConnectorLastAdministratorException.class)
            .hasMessageContaining("last enabled administrator");
    }

    @Test
    void should_accept_an_administrator_when_another_one_is_enabled() {
        var admin = administrator("admin-1");
        when(repository.findEnabledByRoleNameForUpdate(ConnectorRole.DEFAULT_ADMIN_ROLE))
            .thenReturn(List.of(admin, administrator("admin-2")));

        assertThatCode(() -> safeguard.assertAnotherAdministratorRemains(admin))
            .doesNotThrowAnyException();
    }

    @Test
    void should_not_check_a_user_who_is_not_an_administrator() {
        var user = administrator("user-1").toBuilder()
            .roles(Set.of(ConnectorRole.defaultUserRole()))
            .build();

        assertThatCode(() -> safeguard.assertAnotherAdministratorRemains(user))
            .doesNotThrowAnyException();
        verifyNoInteractions(repository);
    }

    @Test
    void should_not_check_an_administrator_who_is_already_disabled() {
        var disabled = administrator("admin-1").toBuilder().enabled(false).build();

        assertThatCode(() -> safeguard.assertAnotherAdministratorRemains(disabled))
            .doesNotThrowAnyException();
        verifyNoInteractions(repository);
    }

    private static ConnectorUser administrator(String uuid) {
        return ConnectorUser.defaultAdminUser().toBuilder().uuid(uuid).build();
    }
}
