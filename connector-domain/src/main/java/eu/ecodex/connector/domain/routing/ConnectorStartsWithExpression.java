/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.routing;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;

/**
 * Represents a match expression that checks if a certain attribute starts with a specific string.
 */
public class ConnectorStartsWithExpression extends ConnectorMatchExpression {
    private final TokenType as4AttributeTokenType;
    private final String startsWith;

    /**
     * Constructs a new ConnectorStartsWithExpression.
     *
     * @param as4AttributeTokenType the type of attribute token to be checked against
     * @param startsWith the string value that the attribute should start with
     */
    ConnectorStartsWithExpression(TokenType as4AttributeTokenType, String startsWith) {
        this.as4AttributeTokenType = as4AttributeTokenType;
        this.startsWith = startsWith;
    }

    @Override
    boolean evaluate(ConnectorMessage message) {
        return ConnectorRoutingRulePattern
                .extractAs4Value(message, as4AttributeTokenType)
                .startsWith(startsWith);
    }
}
