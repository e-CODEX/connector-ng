/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.routing;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the {@code ConnectorExpressionParser}.
 */
@DisplayName("ConnectorMessageRoutingExpressionParser")
public class ConnectorExpressionParserTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "|(&(equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), equals(FromPartyId, 'gw02'))",
            "|(&(startswith(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), startswith(FromPartyId, 'gw02'))",
            "not(|(&(equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), equals(FromPartyId, 'gw02')))"
    })
    void should_parse_successfully_valid_routing_expression_pattern(String pattern) {
        var parser = new ConnectorExpressionParser(pattern);
        var expression = parser.getParsedConnectorExpression();

        assertThat(expression).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "|(&(equals(SrviceName, 'Test'), equals(FromPartyId, 'gw01')), equals(FromPartyId, 'gw02'))",
            "|((equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), equals(FromPartyId, 'gw02'))",
            "|(&(euals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), equals(FromPartyId, 'gw02'))",
            "'@'"
    })
    void should_return_empty_when_routing_expression_pattern_is_invalid(String pattern) {
        var parser = new ConnectorExpressionParser(pattern);
        var expression = parser.getParsedConnectorExpression();
        assertThat(expression).isNotPresent();
        assertThat(parser.getParsingExceptions().size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", })
    void should_throw_illegal_argument_exception_when_routing_expression_pattern_is_empty_or_null(String pattern) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ConnectorExpressionParser(pattern)
        );
    }
}
