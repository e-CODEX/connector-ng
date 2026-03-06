/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest;

import eu.ecodex.connector.infrastructure.inbound.web.rest.advice.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig class for configuring the OpenAPI documentation.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class OpenApiConfig {
    private static final String ERROR_RESPONSE_SCHEMA = "#/components/schemas/ErrorResponse";

    @Bean
    public OpenAPI customOpenAPI() {
        var info = new Info()
                .title("e-CODEX Connector")
                .version("1.0.0")
                .description("Open API documentation for e-CODEX Connector.");

        var resolvedSchema = ModelConverters
                .getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));

        var components = new Components()
                .addSchemas("ErrorResponse", resolvedSchema.schema);

        if (resolvedSchema.referencedSchemas != null) {
            resolvedSchema.referencedSchemas.forEach(components::addSchemas);
        }

        return new OpenAPI()
                .components(components)
                .info(info);
    }

    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (operation, handlerMethod) -> {
            // Only add 500 here — it has no @ApiResponse annotation to conflict with
            addErrorResponse(operation.getResponses(), "500", "Internal Server Error");
            return operation;
        };
    }

    // Runs AFTER all annotation processing — safe to patch 4xx content here
    @Bean
    public OpenApiCustomizer injectErrorResponseSchema() {
        return openApi ->
                openApi
                        .getPaths()
                        .values()
                        .forEach(pathItem ->
                                         pathItem.readOperations()
                                                 .forEach(
                                                 operation ->
                                                         operation.getResponses().forEach(
                                                                 (code, apiResponse) -> {
                                                                     if (code.startsWith("4")
                                                                         || code.startsWith("5")) {
                                                                         apiResponse.content(
                                                                                 getContent()
                                                                         );
                                                                     }
                                                                 })
                                         )
                        );
    }

    private void addErrorResponse(ApiResponses responses, String code, String description) {
        responses.addApiResponse(
                code,
                new ApiResponse()
                        .description(description)
                        .content(getContent())
        );
    }

    private Content getContent() {
        return new Content()
                .addMediaType(
                        "application/json",
                        new MediaType().schema(new Schema<>().$ref(ERROR_RESPONSE_SCHEMA))
                );
    }
}
