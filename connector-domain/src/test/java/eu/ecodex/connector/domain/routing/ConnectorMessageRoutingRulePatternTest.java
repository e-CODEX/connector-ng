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

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.MessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for the {@code ConnectorMessageRoutingRulePattern}.
 */
public class ConnectorMessageRoutingRulePatternTest {
    @ParameterizedTest
    @MethodSource("provideParameters")
    void should_evaluate_routing_pattern_to_true(String expression, ConnectorMessage message) {
        var rulePattern = new ConnectorRoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isTrue();
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        "&(equals(ServiceName, 'Connector-TEST'), |(equals(FromPartyId, 'BL'), "
                        + "equals(FromPartyId, 'gw02')))",
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                ),
                Arguments.of(
                        "&(&(equals(Action, 'ConTest_Form'), equals(ServiceName, "
                        + "'Connector-TEST')), equals(ServiceType, 'urn:e-codex:services:'))",
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                ),
                Arguments.of(
                        "&(startswith(ServiceName, 'Connector-'), &(equals(FromPartyId, 'BL'), "
                        + "equals(ServiceType, 'urn:e-codex:services:')))",
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideNotMatchingParameters")
    void should_evaluate_routing_pattern_to_false(String expression, ConnectorMessage message) {
        var rulePattern = new ConnectorRoutingRulePattern(expression);
        boolean result = rulePattern.matches(message);
        assertThat(result).isFalse();
    }

    private static Stream<Arguments> provideNotMatchingParameters() {
        return Stream.of(
                Arguments.of(
                        "not(&(equals(ServiceName, 'Connector-TEST'), |(equals(FromPartyId, "
                        + "'gw01'), equals(FromPartyId, 'BL'))))",
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                ),
                Arguments.of(
                        "&(&(equals(Action, 'ConTest_Form2'), equals(ServiceName, "
                        + "'Connector-TEST')), equals(ServiceType, 'urn:e-codex:services:'))",
                        MessageTestFixtures.createValidOutboundBusinessMessage()
                )

        );
    }

    @Test
    void should_throw_exception_if_routing_pattern_contains_illegal_token() {
        var expression = "&(equals(ServiceName, 'Connector-TEST'), |(equalss(FromPartyId, 'BL'), "
                         + "equals(FromPartyId, 'gw02')))";
        var message = MessageTestFixtures.createValidOutboundBusinessMessage();
        Assertions.assertThrows(
                ConnectorExpressionParser.ParsingException.class,
                () -> {
                    var rulePattern = new ConnectorRoutingRulePattern(expression);
                    rulePattern.matches(message);
                }
        );
    }
}
