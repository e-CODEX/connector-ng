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

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for specifying Cross-Origin Resource Sharing (CORS) settings.
 *
 * <p>By default, no origin is allowed: a browser application served from another origin, such as
 * the administration UI, can only call the REST API once its origin is listed in
 * {@code connector.cors.allowed-origins}.
 *
 * @param allowedOrigins   the origins allowed to call the REST API; empty to allow none
 * @param allowedMethods   the allowed HTTP methods
 * @param allowedHeaders   the allowed request headers
 * @param allowCredentials whether credentials such as cookies are allowed; the REST API uses
 *                         bearer tokens, which do not need it
 * @param maxAge           the pre-flight response cache duration, in seconds
 */
@Validated
@ConfigurationProperties(prefix = "connector.cors")
public record ConnectorCorsProperties(
    @DefaultValue
    List<String> allowedOrigins,

    @NotEmpty
    @DefaultValue({"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"})
    List<String> allowedMethods,

    @NotEmpty
    @DefaultValue({"Authorization", "Content-Type", "Accept"})
    List<String> allowedHeaders,

    @DefaultValue("false")
    boolean allowCredentials,

    @DefaultValue("3600")
    long maxAge
) {
    /**
     * Canonical constructor: no allowed origin when none is configured.
     */
    public ConnectorCorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }

    /**
     * Tells whether the origins are consistent with the credentials setting: the wildcard origin
     * cannot be combined with credentials, as it would let any website use them.
     *
     * @return {@code false} if credentials are allowed together with the {@code *} origin
     */
    @AssertTrue(message = "connector.cors.allowed-origins cannot contain * when "
        + "connector.cors.allow-credentials is true: list the allowed origins")
    public boolean isWildcardOriginWithoutCredentials() {
        return !allowCredentials || !allowedOrigins.contains("*");
    }
}
