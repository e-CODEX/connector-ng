/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.iam.auth.login;

import eu.ecodex.connector.domain.model.user.ConnectorUser;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of the {@code UserDetails} interface for integrating {@code ConnectorUser}
 * with Spring Security. This class adapts the {@code ConnectorUser} structure to fulfill
 * the contract defined by the {@code UserDetails} interface.
 * <p>
 * The {@code ConnectorUserDetails} class provides information about the authenticated user,
 * such as their username, password, and granted authorities (roles).
 * <p>
 * This class is intended for use in security-related components, such as authentication
 * and authorization within the Spring Security framework.
 * <p>
 * Key responsibilities include:
 * - Adapting the roles of {@code ConnectorUser} to Spring Security's {@code GrantedAuthority}.
 * - Exposing user-specific information, such as the username and password.
 */
@Slf4j
@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserDetails implements UserDetails {

    ConnectorUser connectorUser;

    public ConnectorUserDetails(ConnectorUser connectorUser) {
        this.connectorUser = connectorUser;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return connectorUser.roles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return connectorUser.password();
    }

    @Override
    public String getUsername() {
        return connectorUser.username();
    }
}
