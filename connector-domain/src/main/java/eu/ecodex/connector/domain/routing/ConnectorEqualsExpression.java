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
 * Represents an equality expression used for matching.
 */
public class ConnectorEqualsExpression extends ConnectorMatchExpression {
    private final TokenType as4AttributeTokenType;
    private final String valueString;

    /**
     * Constructs a new {@code ConnectorEqualsExpression} object with the specified token type and
     * value string. This expression is used to check for equality between a value extracted from a
     * message and the provided value string.
     *
     * @param as4AttributeTokenType the token type representing the AS4 attribute to be evaluated
     * @param valueString           the string value to be compared with the extracted value
     */
    public ConnectorEqualsExpression(TokenType as4AttributeTokenType, String valueString) {
        this.as4AttributeTokenType = as4AttributeTokenType;
        this.valueString = valueString;
    }

    @Override
    boolean evaluate(ConnectorMessage message) {
        return valueString.equals(
                ConnectorRoutingRulePattern.extractAs4Value(message, as4AttributeTokenType)
        );
    }
}
