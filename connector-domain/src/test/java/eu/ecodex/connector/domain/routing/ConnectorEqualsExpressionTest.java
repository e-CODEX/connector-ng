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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorEqualsExpression}.
 */
@DisplayName("ConnectorMessageRoutingEqualsExpression")
public class ConnectorEqualsExpressionTest {

    private final ConnectorBusinessMessage message =
        BusinessMessageTestFixtures.createOutboundMessage();

    @Test
    void should_return_true_when_the_attribute_matches_the_value() {
        var equalsExpression = new ConnectorEqualsExpression(TokenType.AS4_ACTION, "ConTest_Form");

        assertThat(equalsExpression.evaluate(message)).isTrue();
    }

    @Test
    void should_return_false_when_the_attribute_does_not_match_the_value() {
        var equalsExpression = new ConnectorEqualsExpression(TokenType.AS4_ACTION, "ConTest_Form2");

        assertThat(equalsExpression.evaluate(message)).isFalse();
    }
}
