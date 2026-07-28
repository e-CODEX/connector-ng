/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.auth.login;

import eu.ecodex.connector.application.service.iam.auth.ConnectorLoginUserService;
import eu.ecodex.connector.domain.model.login.LoginResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.login.LoginRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling user login operations for the connector system.
 * This class implements the {@link ConnectorUserLoginApi} interface, providing API functionality
 * for authenticating users and returning an access token upon successful login.
 * <p>
 * The login process involves validating user credentials and generating a token
 * using the provided {@code ConnectorLoginUserService}.
 * <p>
 * Annotations:
 * - {@code @Slf4j}: Enables logging within the class.
 * - {@code @RestController}: Marks the class as a REST controller, allowing it
 *   to handle HTTP requests and return responses in a RESTful manner.
 * - {@code @RequiredArgsConstructor}: Automatically generates a constructor with
 *   required arguments for final fields.
 * - {@code @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)}:
 *   Ensures all fields are private and final by default.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectorUserLoginController implements ConnectorUserLoginApi {

    ConnectorLoginUserService connectorLoginUserService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return connectorLoginUserService.login(loginRequest.username(),
                loginRequest.password());

    }
}
