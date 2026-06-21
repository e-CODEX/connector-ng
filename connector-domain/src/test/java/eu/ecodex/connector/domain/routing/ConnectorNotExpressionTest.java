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

import eu.ecodex.connector.MessageTestFixtures;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorNotExpression}.
 */
public class ConnectorNotExpressionTest {
    @Test
    void expression_should_not_equals_is_true() {
        var equalsExpression = new ConnectorEqualsExpression(TokenType.AS4_ACTION, "ConTest");
        var notExpression = new ConnectorNotExpression(equalsExpression, null);

        var evaluationResult = notExpression.evaluate(
                MessageTestFixtures.createOutboundBusinessMessage()
        );

        assertThat(evaluationResult).isTrue();
    }

    @Test
    void expression_should_not_equals_is_false() {
        var equalsExpression = new ConnectorEqualsExpression(TokenType.AS4_ACTION, "ConTest_Form");
        var notExpression = new ConnectorNotExpression(equalsExpression, null);

        var evaluationResult = notExpression.evaluate(
                MessageTestFixtures.createOutboundBusinessMessage()
        );

        assertThat(evaluationResult).isFalse();
    }
}
