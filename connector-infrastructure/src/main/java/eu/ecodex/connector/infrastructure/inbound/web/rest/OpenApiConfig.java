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
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig class for configuring the OpenAPI documentation.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class OpenApiConfig {
    private static final String ERROR_RESPONSE = "ErrorResponse";
    private static final String ERROR_RESPONSE_SCHEMA =
        "#/components/schemas/" + ERROR_RESPONSE;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                      .title("e-CODEX Connector")
                      .version("1.0.0")
                      .description("Open API documentation for e-CODEX Connector."));
    }

    @Bean
    public GroupedOpenApi publicApi(OpenApiCustomizer injectErrorResponseSchema) {
        return GroupedOpenApi.builder()
                             .group("public")
                             .pathsToMatch("/api/**")
                             .pathsToExclude("/api/v?/admin/**")
                             .addOpenApiCustomizer(injectErrorResponseSchema)
                             .build();
    }

    @Bean
    public GroupedOpenApi adminApi(OpenApiCustomizer injectErrorResponseSchema) {
        return GroupedOpenApi.builder()
                             .group("admin")
                             .pathsToMatch("/api/v?/admin/**")
                             .addOpenApiCustomizer(injectErrorResponseSchema)
                             .build();
    }

    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (operation, handlerMethod) -> {
            // Only 500 here: it has no @ApiResponse annotation to conflict with
            addErrorResponse(operation.getResponses());
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer injectErrorResponseSchema() {
        return openApi -> {
            registerErrorSchemas(openApi);

            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths()
                   .values()
                   .forEach(pathItem ->
                                pathItem.readOperations().forEach(operation -> {
                                    if (operation.getResponses() == null) {
                                        return;
                                    }
                                    operation.getResponses().forEach((code, apiResponse) -> {
                                        if (code.startsWith("4") || code.startsWith("5")) {
                                            apiResponse.content(getContent());
                                        }
                                    });
                                }));
        };
    }

    private void registerErrorSchemas(OpenAPI openApi) {
        var resolved = ModelConverters.getInstance()
                                      .resolveAsResolvedSchema(
                                          new AnnotatedType(ErrorResponse.class)
                                      );

        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        var components = openApi.getComponents();

        components.addSchemas(ERROR_RESPONSE, resolved.schema);
        if (resolved.referencedSchemas != null) {
            resolved.referencedSchemas.forEach(components::addSchemas);
        }
    }

    private void addErrorResponse(ApiResponses responses) {
        responses.addApiResponse(
            "500",
            new ApiResponse()
                .description("Internal Server Error")
                .content(getContent())
        );
    }

    private Content getContent() {
        return new Content().addMediaType(
            "application/json",
            new MediaType().schema(new Schema<>().$ref(ERROR_RESPONSE_SCHEMA))
        );
    }
}
