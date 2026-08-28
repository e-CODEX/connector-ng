/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.container.check;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.infrastructure.outbound.security.container.checks.ConnectorContainerTokenIssuerChecker;
import eu.ecodex.connector.infrastructure.outbound.security.model.container.ConnectorContainer;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.outbound.security.model.token.ConnectorTokenIssuer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("DataFlowIssue")
@DisplayName("ConnectorContainerTokenIssuerChecker")
public class ConnectorContainerTokenIssuerCheckerTest {
    private final ConnectorContainerTokenIssuerChecker checker = new ConnectorContainerTokenIssuerChecker();

    private ConnectorContainer validIssuer(String country) {
        var issuer = mock(ConnectorTokenIssuer.class, RETURNS_MOCKS);
        when(issuer.getServiceProvider()).thenReturn("Some Service Provider");
        when(issuer.getCountry()).thenReturn(country);

        var token = mock(ConnectorToken.class);

        var container = mock(ConnectorContainer.class);
        when(container.token()).thenReturn(token);
        when(container.token().getIssuer()).thenReturn(issuer);

        return container;
    }

    @Nested
    @DisplayName("valid issuer")
    class Valid {
        @Test
        void should_accept_a_fully_populated_issuer() {
            assertThatCode(() -> checker.check(validIssuer("FR")))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"FR", "US", "DE", "BE", "LU"})
        void should_accept_valid_iso_country_codes(String country) {
            assertThatCode(() -> checker.check(validIssuer(country)))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"fr", "us", "De", "bE"})
        void should_accept_country_codes_case_insensitively(String country) {
            assertThatCode(() -> checker.check(validIssuer(country)))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("invalid issuer")
    class Invalid {
        @Test
        void should_reject_null_issuer() {
            assertThatThrownBy(() -> checker.check(null))
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "\t"})
        void should_reject_blank_service_provider(String serviceProvider) {
            var issuer = mock(ConnectorTokenIssuer.class);
            when(issuer.getServiceProvider()).thenReturn(serviceProvider);
            var token = mock(ConnectorToken.class);
            var container = mock(ConnectorContainer.class);
            when(container.token()).thenReturn(token);
            when(container.token().getIssuer()).thenReturn(issuer);

            assertThatThrownBy(() -> checker.check(container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("service provider");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "\t"})
        void should_reject_blank_country(String country) {
            var issuer = mock(ConnectorTokenIssuer.class);
            when(issuer.getServiceProvider()).thenReturn("Some Service Provider");
            when(issuer.getCountry()).thenReturn(country);
            var token = mock(ConnectorToken.class);
            var container = mock(ConnectorContainer.class);
            when(container.token()).thenReturn(token);
            when(container.token().getIssuer()).thenReturn(issuer);

            assertThatThrownBy(() -> checker.check(container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("country");
        }

        @ParameterizedTest
        @ValueSource(strings = {"ZZ", "XX", "QQ", "F", "FRA", "42"})
        void should_reject_non_iso_country_codes(String country) {
            var issuer = mock(ConnectorTokenIssuer.class);
            when(issuer.getServiceProvider()).thenReturn("Some Service Provider");
            when(issuer.getCountry()).thenReturn(country);
            var token = mock(ConnectorToken.class);
            var container = mock(ConnectorContainer.class);
            when(container.token()).thenReturn(token);
            when(container.token().getIssuer()).thenReturn(issuer);

            assertThatThrownBy(() -> checker.check(container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ISO 3166-1");
        }

        @Test
        void should_reject_missing_advanced_electronic_system() {
            var issuer = mock(ConnectorTokenIssuer.class);
            when(issuer.getServiceProvider()).thenReturn("Some Service Provider");
            when(issuer.getCountry()).thenReturn("FR");
            var token = mock(ConnectorToken.class);
            var container = mock(ConnectorContainer.class);
            when(container.token()).thenReturn(token);
            when(container.token().getIssuer()).thenReturn(issuer);

            assertThatThrownBy(() -> checker.check(container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("advanced electronic system");
        }
    }
}

