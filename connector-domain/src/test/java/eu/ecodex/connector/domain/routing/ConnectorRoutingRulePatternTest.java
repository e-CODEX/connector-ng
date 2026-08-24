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

import eu.ecodex.connector.BusinessMessageTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@code ConnectorRoutingRulePattern}.
 */
@DisplayName("ConnectorRoutingRulePattern")
public class ConnectorRoutingRulePatternTest {
    @Test
    void should_fail_when_attribute_to_extract_is_not_an_as4_attribute() {
        var message = BusinessMessageTestFixtures.createOutboundMessage();

        assertThrows(
            IllegalStateException.class,
            () -> ConnectorRoutingRulePattern.extractAs4Value(message, TokenType.OR)
        );
    }
}
