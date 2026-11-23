/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest;

import eu.ecodex.connector.infrastructure.adapter.in.web.rest.advice.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

/**
 * OpenApiConfig class for configuring the OpenAPI documentation.
 */
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class OpenApiConfig {
    private static final String ERROR_RESPONSE = "ErrorResponse";
    private static final String ERROR_RESPONSE_SCHEMA =
        "#/components/schemas/" + ERROR_RESPONSE;
    private static final String BEARER_AUTH = "bearerAuth";
    private final Map<String, Schema<?>> successSchemas = new ConcurrentHashMap<>();

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                      .title("e-CODEX Connector")
                      .version("1.0.0")
                      .description("Open API documentation for e-CODEX Connector."))
            .components(new Components().addSecuritySchemes(
                BEARER_AUTH,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            ));
    }

    @Bean
    public GroupedOpenApi publicApi(
        OpenApiCustomizer injectErrorResponseSchema,
        OperationCustomizer globalResponseCustomizer) {
        return GroupedOpenApi.builder()
                             .group("public")
                             .pathsToMatch("/api/**")
                             .pathsToExclude("/api/v?/admin/**")
                             .addOpenApiCustomizer(injectErrorResponseSchema)
                             .addOperationCustomizer(globalResponseCustomizer)
                             .build();
    }

    @Bean
    public GroupedOpenApi adminApi(
        OpenApiCustomizer injectErrorResponseSchema,
        OperationCustomizer globalResponseCustomizer) {
        return GroupedOpenApi.builder()
                             .group("admin")
                             .pathsToMatch("/api/v?/admin/**")
                             .addOpenApiCustomizer(injectErrorResponseSchema)
                             .addOperationCustomizer(globalResponseCustomizer)
                             .addOpenApiCustomizer(
                                 openApi ->
                                     openApi.addSecurityItem(new SecurityRequirement().addList(
                                         BEARER_AUTH)))
                             .build();
    }

    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (operation, handlerMethod) -> {
            addSuccessResponse(operation, handlerMethod);
            // 500 has no @ApiResponse annotation to conflict with
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

    private void addSuccessResponse(Operation operation, HandlerMethod handlerMethod) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }
        var responses = operation.getResponses();
        if (responses.keySet().stream().anyMatch(code -> code.startsWith("2"))) {
            return;
        }

        var returnType = ResolvableType.forMethodReturnType(handlerMethod.getMethod());
        if (ResponseEntity.class.isAssignableFrom(returnType.toClass())) {
            returnType = returnType.getGeneric(0);
        }
        var rawType = returnType.toClass();
        var isVoid = rawType == void.class || rawType == Void.class;

        var status = resolveSuccessStatus(handlerMethod, isVoid);

        var response = new ApiResponse().description(status.getReasonPhrase());
        if (!isVoid) {
            var schema = resolveSuccessSchema(returnType.getType(), rawType);
            response.content(new Content().addMediaType("*/*", new MediaType().schema(schema)));
        }
        responses.addApiResponse(String.valueOf(status.value()), response);
    }

    private HttpStatus resolveSuccessStatus(HandlerMethod handlerMethod, boolean isVoid) {
        var responseStatus = handlerMethod.getMethodAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return isVoid ? HttpStatus.NO_CONTENT : HttpStatus.OK;
    }

    private Schema<?> resolveSuccessSchema(Type type, Class<?> rawType) {
        if (Resource.class.isAssignableFrom(rawType) || rawType == byte[].class) {
            return new BinarySchema();
        }

        var resolved = ModelConverters.getInstance()
                                      .resolveAsResolvedSchema(
                                          new AnnotatedType(type).resolveAsRef(true)
                                      );
        if (resolved.referencedSchemas != null) {
            resolved.referencedSchemas.forEach(successSchemas::putIfAbsent);
        }

        return resolved.schema;
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

        successSchemas.forEach((name, schema) -> {
            if (components.getSchemas() == null || !components.getSchemas().containsKey(name)) {
                components.addSchemas(name, schema);
            }
        });
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
