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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorStartsWithExpression}.
 */
@DisplayName("ConnectorMessageRoutingStartsWithExpression")
public class ConnectorStartsWithExpressionTest {
    @Test
    void should_return_true_when_as4_attribute_starts_with_given_value() {
        var message = BusinessMessageTestFixtures.createOutboundMessage();
        var startsWithExpression = new ConnectorStartsWithExpression(
            TokenType.AS4_ACTION, "ConTest"
        );
        var evaluationResult = startsWithExpression.evaluate(message);
        assertThat(evaluationResult).isTrue();
    }

    @Test
    void should_return_false_when_as4_attribute_does_not_start_with_given_value() {
        var message = BusinessMessageTestFixtures.createOutboundMessage();
        var startsWithExpression = new ConnectorStartsWithExpression(TokenType.AS4_ACTION, "Hello");
        var evaluationResult = startsWithExpression.evaluate(message);
        assertThat(evaluationResult).isFalse();
    }
}
