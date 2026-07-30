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
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The routing rule grammar. {@literal ##BNF ConnectorRoutingRulePattern tag::BNF[]
 * <ROUTING_RULE_PATTERN> ::= <BOOLEAN_EXPRESSION> | <COMPARE_EXPRESSION> | <NOT_EXPRESSION>
 * <BOOLEAN_EXPRESSION> ::= <OPERAND>(<ROUTING_RULE_PATTERN>, <ROUTING_RULE_PATTERN>)
 * <COMPARE_EXPRESSION> ::= equals(<AS4_TYPE>, '<VALUE>') | startswith(<AS4_TYPE>, '<VALUE>')
 * <NOT_EXPRESSION> ::= not(<ROUTING_RULE_PATTERN>)
 * <OPERAND> ::= "&" | "|"
 * <AS4_TYPE> ::= ServiceType | ServiceName | FinalRecipient | Action | FromPartyId | FromPartyRole
 * | FromPartyIdType
 * <VALUE> ::= <VALUE><LETTER> | <LETTER>
 * <LETTER> can be every letter [a-z][A-Z][0-9] other printable characters might work, but
 * they untested! ['\|&)( will definitiv not work!]
 * <p>
 * end::BNF[] ##BNF}
 */
@Getter
@Slf4j
public class ConnectorRoutingRulePattern {
    private final String matchRule;
    private ConnectorExpression connectorExpression;

    /**
     * Constructs an instance of ConnectorRoutingRulePattern using the provided pattern. The pattern
     * defines the routing rule logic in a specific grammar for matching connector messages.
     *
     * @param pattern the routing rule pattern to be used for matching. It should adhere to the
     *                defined grammar structure, which includes logical and comparison expressions.
     *                Invalid or unparsable patterns will throw a parsing exception.
     */
    public ConnectorRoutingRulePattern(String pattern) {
        this.matchRule = pattern;
        createMatcher(pattern);
    }

    static String extractAs4Value(ConnectorMessage message, TokenType as4Attribute) {
        ConnectorMessageAS4Properties as4Properties = message.as4Properties();
        if (as4Properties.service() == null) {
            throw new IllegalStateException("Cannot extract AS4 value without a service");
        }

        if (as4Properties.fromParty() == null) {
            throw new IllegalStateException("Cannot extract AS4 value without a fromParty");
        }

        if (as4Properties.action() == null) {
            throw new IllegalStateException("Cannot extract AS4 value without a action");
        }

        return switch (as4Attribute) {
            case TokenType.AS4_SERVICE_NAME -> as4Properties.service().name();
            case TokenType.AS4_SERVICE_TYPE -> as4Properties.service().type();
            case TokenType.AS4_ACTION -> as4Properties.action().name();
            case TokenType.AS4_FINAL_RECIPIENT -> as4Properties.finalRecipient();
            case TokenType.AS4_FROM_PARTY_ID_TYPE -> as4Properties.fromParty().identifierType();
            case TokenType.AS4_FROM_PARTY_ID -> as4Properties.fromParty().identifier();
            case TokenType.AS4_FROM_PARTY_ROLE -> as4Properties.fromParty().role();
            default -> throw new IllegalStateException("Unsupported AS4 Attribute to match!");
        };
    }

    private void createMatcher(final String pattern) {
        // TODO: error handling...
        var expressionParser = new ConnectorExpressionParser(pattern);
        var parser = expressionParser.getParsedConnectorExpression();
        if (parser.isPresent()) {
            this.connectorExpression = parser.get();
        } else {
            log.error("Could not parse routing rule pattern: {}", pattern);

            throw new ConnectorExpressionParser.ParsingException(
                    "Could not parse routing rule pattern: " + pattern);
        }
    }

    public boolean matches(ConnectorMessage message) {
        return this.connectorExpression.evaluate(message);
    }
}
