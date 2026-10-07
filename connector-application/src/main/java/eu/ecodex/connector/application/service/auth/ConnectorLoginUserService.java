/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.auth;

import eu.ecodex.connector.application.port.api.auth.ConnectorLoginUser;
import eu.ecodex.connector.application.port.spi.auth.login.ConnectorUserAuthenticationProvider;
import eu.ecodex.connector.domain.model.auth.ConnectorUserAuthenticationResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This service implements the user login to the connector system.
 */
@Slf4j
@Service
public class ConnectorLoginUserService implements ConnectorLoginUser {
    private final ConnectorUserAuthenticationProvider userAuthenticationProvider;

    public ConnectorLoginUserService(
        ConnectorUserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Override
    public ConnectorUserAuthenticationResult execute(@NonNull String username,
                                                     @NonNull String password) {
        return userAuthenticationProvider.login(username, password);
    }
}
