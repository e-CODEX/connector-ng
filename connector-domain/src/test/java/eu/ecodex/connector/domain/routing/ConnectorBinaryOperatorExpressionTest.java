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

import eu.ecodex.connector.MessageTestFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorBinaryOperatorExpression}.
 */
public class ConnectorBinaryOperatorExpressionTest {
    // OR expression
    @Test
    void expression_or_is_evaluated_positively_if_expressions_1_and_2_are_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.OR, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isTrue();
    }

    @Test
    void expression_or_is_evaluated_positively_if_only_expression_1_is_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_FINAL_RECIPIENT, "bob");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.OR, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isTrue();
    }

    @Test
    void expression_or_is_evaluated_positively_if_only_expression_2_is_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_FROM_PARTY_ID_TYPE, "Connector-Incorrect");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.OR, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isTrue();
    }

    // AND expression
    @Test
    void expression_and_is_evaluated_positively_if_expressions_1_and_2_are_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.AND, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isTrue();
    }

    @Test
    void expression_or_is_evaluated_negatively_if_expressions_1_and_2_are_false() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_FROM_PARTY_ROLE, "Incorrect");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.OR, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isFalse();
    }

    @Test
    void expression_and_is_evaluated_negatively_if_only_expression_1_is_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.AND, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isFalse();
    }

    @Test
    void expression_and_is_evaluated_negatively_if_only_expression_2_is_true() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.AND, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isFalse();
    }

    @Test
    void expression_and_is_evaluated_negatively_if_expressions_1_and_2_are_false() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Incorrect");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Incorrect");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.AND, expression1, expression2
        );

        var evaluationResult = binaryOrExpression.evaluate(message);
        assertThat(evaluationResult).isFalse();
    }

    @Test
    void should_throw_exception_if_binary_expression_operand_is_different_from_and_or_or() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();
        var expression1 = new ConnectorEqualsExpression(TokenType.AS4_SERVICE_NAME, "Connector-TEST");
        var expression2 = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "ConTest");

        var binaryOrExpression = new ConnectorBinaryOperatorExpression(
                TokenType.NOT, expression1, expression2
        );

        Assertions.assertThrows(
                RuntimeException.class,
                () -> binaryOrExpression.evaluate(message)
        );
    }
}
