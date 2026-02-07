/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig class for configuring the OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {
    /**
     * Configures and returns a custom OpenAPI configuration for the application. The OpenAPI
     * instance includes metadata such as the API title, version, and description.
     *
     * @return the configured {@code OpenAPI} instance containing metadata for the API
     *         documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        var info = new Info()
                .title("e-CODEX Connector")
                .version("1.0.0")
                .description("Open API documentation for e-CODEX Connector.");

        return new OpenAPI().info(info);
    }
}
