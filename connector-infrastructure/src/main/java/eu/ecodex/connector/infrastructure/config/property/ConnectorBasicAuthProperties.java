/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config.property;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties of the HTTP Basic credentials protecting the Swagger UI and the OpenAPI
 * documents.
 *
 * @param username the Basic authentication username
 * @param password the Basic authentication password
 */
@Validated
@ConfigurationProperties(prefix = "connector.basic")
public record ConnectorBasicAuthProperties(
    @NotBlank String username,
    @NotBlank String password
) {
}
