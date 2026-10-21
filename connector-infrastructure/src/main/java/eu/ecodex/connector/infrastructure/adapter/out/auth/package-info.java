/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/**
 * Outbound authentication adapters of the connector: the implementations through which the core
 * authenticates users and protects access to its functionality.
 *
 * <p>Classes in this package implement the authentication-related outbound ports of
 * {@link eu.ecodex.connector.application.port.spi}, such as verifying credentials, hashing and
 * checking passwords, and issuing or validating access tokens. They use the concrete security
 * technology (Spring Security, a token library) and translate its results into domain types, so
 * that no security framework details reach the core.
 *
 * <p>Authentication adapters contain no business rules: deciding who may do what (roles,
 * permissions, account status) belongs to the domain model and the user services of the application
 * layer.
 */
package eu.ecodex.connector.infrastructure.adapter.out.auth;
