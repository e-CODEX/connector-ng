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

import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.AS4PropertiesTestFixtures;
import eu.ecodex.connector.MessageTestFixtures;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorRoutingRulePattern}.
 */
public class ConnectorRoutingRulePatternTest {
    @Test
    void should_fail_when_attribute_to_extract_is_not_an_as4_attribute() {
        var message = MessageTestFixtures.createOutboundBusinessMessage();

        assertThrows(
            IllegalStateException.class,
            () -> ConnectorRoutingRulePattern.extractAs4Value(message, TokenType.OR)
        );
    }

    @Test
    void should_fail_if_message_as4_properties_service_is_null() {
        var message = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .as4Properties(
                                             AS4PropertiesTestFixtures.createAS4PropertiesWithoutService()
                                         )
                                         .build();

        assertThrows(
            IllegalStateException.class,
            () -> ConnectorRoutingRulePattern.extractAs4Value(message, TokenType.OR)
        );
    }

    @Test
    void should_fail_if_message_as4_properties_from_party_is_null() {
        var message = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .as4Properties(
                                             AS4PropertiesTestFixtures.createAS4PropertiesWithoutFromParty()
                                         )
                                         .build();

        assertThrows(
            IllegalStateException.class,
            () -> ConnectorRoutingRulePattern.extractAs4Value(message, TokenType.OR)
        );
    }

    @Test
    void should_fail_if_message_as4_properties_action_is_null() {
        var message = MessageTestFixtures.createOutboundBusinessMessage()
                                         .toBuilder()
                                         .as4Properties(
                                             AS4PropertiesTestFixtures.createAS4PropertiesWithoutAction()
                                         )
                                         .build();

        assertThrows(
            IllegalStateException.class,
            () -> ConnectorRoutingRulePattern.extractAs4Value(message, TokenType.OR)
        );
    }
}
