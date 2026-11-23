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

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

@DisplayName("ConnectorCorsProperties")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class ConnectorCorsPropertiesTest {
    private static ConnectorCorsProperties bind(Map<String, String> properties) {
        return new Binder(new MapConfigurationPropertySource(properties))
            .bindOrCreate("connector.cors", ConnectorCorsProperties.class);
    }

    private static boolean isValid(ConnectorCorsProperties properties) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().validate(properties).isEmpty();
        }
    }

    @Test
    void should_allow_no_origin_and_no_credentials_by_default() {
        var properties = bind(Map.of());

        assertThat(properties.allowedOrigins()).isEmpty();
        assertThat(properties.allowCredentials()).isFalse();
        assertThat(properties.allowedHeaders())
            .containsExactly("Authorization", "Content-Type", "Accept");
        assertThat(properties.allowedMethods())
            .containsExactly("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(isValid(properties)).isTrue();
    }

    @Test
    void should_bind_the_configured_origins() {
        var properties = bind(Map.of(
            "connector.cors.allowed-origins", "https://admin.example.org,http://localhost:3000"
        ));

        assertThat(properties.allowedOrigins())
            .containsExactly("https://admin.example.org", "http://localhost:3000");
        assertThat(isValid(properties)).isTrue();
    }

    @Test
    void should_accept_the_wildcard_origin_without_credentials() {
        var properties = bind(Map.of("connector.cors.allowed-origins", "*"));

        assertThat(isValid(properties)).isTrue();
    }

    @Test
    void should_reject_the_wildcard_origin_with_credentials() {
        var properties = bind(Map.of(
            "connector.cors.allowed-origins", "*",
            "connector.cors.allow-credentials", "true"
        ));

        assertThat(isValid(properties)).isFalse();
    }

    @Test
    void should_accept_credentials_with_explicit_origins() {
        var properties = bind(Map.of(
            "connector.cors.allowed-origins", "https://admin.example.org",
            "connector.cors.allow-credentials", "true"
        ));

        assertThat(isValid(properties)).isTrue();
    }
}
