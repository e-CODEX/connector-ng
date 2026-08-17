/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth.login;

import eu.ecodex.connector.application.exception.ConnectorUserNotFoundException;
import eu.ecodex.connector.application.port.api.auth.user.ConnectorRetrieveUser;
import eu.ecodex.connector.domain.model.user.ConnectorUser;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserDetailsService} interface for loading user-specific data.
 * This class primarily uses the {@link ConnectorRetrieveUser} service to fetch user details
 * based on the provided username and adapt them to a format compatible with Spring Security.
 *
 * <p>Responsibilities:
 * - Retrieve user data from the {@link ConnectorRetrieveUser} service.
 * - Convert the retrieved {@link ConnectorUser} object into a {@link UserDetails} instance.
 * - Throw a {@link UsernameNotFoundException} if the user is not found.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserDetailsService implements UserDetailsService {

    ConnectorRetrieveUser retrieveUser;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username)
            throws ConnectorUserNotFoundException {

        ConnectorUser connectorUser = retrieveUser.getByUsername(username);
        return new ConnectorUserDetails(connectorUser);
    }
}
