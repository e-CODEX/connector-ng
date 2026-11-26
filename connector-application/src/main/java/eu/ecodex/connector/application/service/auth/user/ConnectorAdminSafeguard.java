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

import eu.ecodex.connector.application.exception.user.ConnectorLastAdministratorException;
import eu.ecodex.connector.application.port.spi.auth.user.ConnectorUserRepository;
import eu.ecodex.connector.domain.model.user.ConnectorRole;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps at least one enabled administrator: the administration API could not be used any more
 * without one.
 *
 * <p>The enabled administrators are read with a lock held until the end of the transaction, so two
 * administrators removing each other at the same time cannot both succeed. It must therefore be
 * called within the transaction which changes the user.
 */
@Slf4j
@Component
@Transactional(propagation = Propagation.MANDATORY)
public class ConnectorAdminSafeguard {
    private final ConnectorUserRepository repository;

    public ConnectorAdminSafeguard(ConnectorUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Checks that another enabled administrator remains once the given user is removed, disabled or
     * no longer an administrator. Nothing is checked for a user who is not an enabled
     * administrator.
     *
     * @param user the user about to lose its administrator access, as it is now
     *
     * @throws ConnectorLastAdministratorException if the user is the last enabled administrator
     */
    public void assertAnotherAdministratorRemains(@NonNull ConnectorUser user) {
        if (!user.enabled() || !user.roleNames().contains(ConnectorRole.DEFAULT_ADMIN_ROLE)) {
            return;
        }

        var anotherRemains = repository
            .findEnabledByRoleNameForUpdate(ConnectorRole.DEFAULT_ADMIN_ROLE)
            .stream()
            .anyMatch(administrator -> !administrator.uuid().equals(user.uuid()));

        if (!anotherRemains) {
            log.warn(
                "Refused to leave the connector without an enabled administrator, user [{}]",
                user.uuid()
            );
            throw new ConnectorLastAdministratorException(
                "The last enabled administrator cannot be removed, disabled or demoted");
        }
    }
}
