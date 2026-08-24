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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorBinaryOperatorExpression}.
 */

@DisplayName("ConnectorMessageRoutingBinaryOperatorExpression")
public class ConnectorBinaryOperatorExpressionTest {
    private final ConnectorBusinessMessage message =
        BusinessMessageTestFixtures.createOutboundMessage();

    @Nested
    @DisplayName("OR operator")
    class OrOperator {
        @Test
        void should_be_true_when_both_expressions_are_true() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.OR, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isTrue();
        }

        @Test
        void should_be_true_when_only_the_first_expression_is_true() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_FINAL_RECIPIENT, "bob");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.OR, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isTrue();
        }

        @Test
        void should_be_true_when_only_the_second_expression_is_true() {
            var expression1 =
                new ConnectorEqualsExpression(
                    TokenType.AS4_FROM_PARTY_ID_TYPE,
                    "Connector-Incorrect"
                );
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.OR, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isTrue();
        }

        @Test
        void should_be_false_when_both_expressions_are_false() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_FROM_PARTY_ROLE, "Incorrect");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.OR, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isFalse();
        }
    }

    @Nested
    @DisplayName("AND operator")
    class AndOperator {
        @Test
        void should_be_true_when_both_expressions_are_true() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.AND, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isTrue();
        }

        @Test
        void should_be_false_when_only_the_first_expression_is_true() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.AND, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isFalse();
        }

        @Test
        void should_be_false_when_only_the_second_expression_is_true() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.AND, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isFalse();
        }

        @Test
        void should_be_false_when_both_expressions_are_false() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.AND, expression1, expression2);

            assertThat(binaryExpression.evaluate(message)).isFalse();
        }
    }

    @Nested
    @DisplayName("unsupported operator")
    class UnsupportedOperator {
        @Test
        void should_fail_when_the_operator_is_neither_and_nor_or() {
            var expression1 =
                new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
            var expression2 =
                new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

            var binaryExpression =
                new ConnectorBinaryOperatorExpression(TokenType.NOT, expression1, expression2);

            assertThrows(
                RuntimeException.class,
                () -> binaryExpression.evaluate(message)
            );
        }
    }
}
