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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;


/**
 * The ConnectorExpressionParser class is used to parse an input pattern and generate a
 * ConnectorExpression object based on the pattern.
 */
@Getter
@SuppressWarnings("squid:S1135")
public class ConnectorExpressionParser {
    // TODO: list with parsing errors...
    private static final Token START_TOKEN_VALUE = new Token();

    static {
        START_TOKEN_VALUE.type = TokenType.START_TOKEN;
        START_TOKEN_VALUE.value = "";
        START_TOKEN_VALUE.start = 0;
        START_TOKEN_VALUE.end = 0;
    }

    private final String pattern;
    private final List<ParsingException> parsingExceptions;
    private final LinkedList<Token> tokens;
    private ConnectorExpression parsedConnectorExpression;
    private Token lastConsumedToken;

    /**
     * Constructs a new instance of the {@code ConnectorExpressionParser} class with the provided
     * pattern. The constructor initializes the parser with the given pattern and immediately parses
     * the expression to tokenize and process it into an evaluable form.
     *
     * @param pattern the pattern string to be parsed; cannot be null or blank
     *
     * @throws IllegalArgumentException if the provided pattern is null, empty, or only contains
     *                                  whitespace
     */
    public ConnectorExpressionParser(String pattern) {
        if (StringUtils.isAllBlank(pattern)) {
            throw new IllegalArgumentException("pattern is not allowed to be empty or null!");
        }

        this.pattern = pattern;
        tokens = new LinkedList<>();
        parsingExceptions = new ArrayList<>();

        parseExpression();
    }

    private void parseExpression() {
        var parsing = new StringBuilder();
        parsing.append(pattern.trim());
        var token = getToken(parsing, 0);

        while (token.type != TokenType.END_TOKEN
               && token.type != TokenType.ILLEGAL_TOKEN) {
            if (token.type != TokenType.WHITESPACE) {
                tokens.add(token);
            }
            token = getToken(parsing, token.end);
        }

        // convert list of tokens into matchers
        lastConsumedToken = START_TOKEN_VALUE;

        try {
            this.parsedConnectorExpression = createExpressions();
        } catch (ParsingException p) {
            this.parsingExceptions.add(p);
        }
    }

    private Token getToken(StringBuilder parsing, int offset) {
        if (parsing.isEmpty()) {
            var token = new Token();
            token.type = TokenType.END_TOKEN;
            token.start = offset;
            token.end = offset;
            token.value = "";
            return token;
        }

        int lastMatchingCharacterIndex = 0;
        TokenType matchingTokenType = TokenType.ILLEGAL_TOKEN;
        int tokenEndOffset = parsing.length();

        for (TokenType tokenType : TokenType.values()) {
            lastMatchingCharacterIndex = tokenType.getLastMatchingCharacter(parsing.toString());
            if (lastMatchingCharacterIndex != -1) {
                matchingTokenType = tokenType;
                tokenEndOffset = lastMatchingCharacterIndex;
                break;
            }
        }

        // when no valid token could be parsed, an illegal token is used as a placeholder
        var token = new Token();
        token.type = matchingTokenType;
        token.start = offset;
        token.end = offset + tokenEndOffset;
        token.value = parsing.substring(0, lastMatchingCharacterIndex);

        parsing.delete(0, lastMatchingCharacterIndex);

        return token;
    }

    private ConnectorExpression createExpressions() {
        var token = getOneOfExpectedTokenFromListOrThrow(TokenType.ALL_OPERATOR_TOKEN_TYPES);
        var tokenType = token.type;

        lastConsumedToken = token;

        ConnectorExpression connectorExpression;

        if (tokenType == TokenType.OR || tokenType == TokenType.AND) {
            // parsing BOOLEAN_EXPRESSION
            connectorExpression = processBooleanToken(token);
        } else if (tokenType == TokenType.EQUALS || tokenType == TokenType.STARTSWITH) {
            // parsing matching connectorExpression
            connectorExpression = processCompareToken(token);
        } else if (tokenType == TokenType.NOT) {
            connectorExpression = processNotToken(token);
        } else {
            throw new ParsingException(
                    lastConsumedToken, token, String.format(
                    "parsing error at %d: I would expect one of these: %s", token.end + 1,
                    TokenType.ALL_OPERATOR_TOKEN_TYPES
                            .stream()
                            .map(Enum::toString)
                            .collect(Collectors.joining(","))
            )
            );
        }

        return connectorExpression;
    }

    private ConnectorExpression processNotToken(Token notToken) {
        getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);
        var expression = createExpressions();
        getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return new ConnectorNotExpression(expression, notToken);
    }

    private ConnectorExpression processCompareToken(Token operatorToken) {
        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);

        var as4AttributeToken = getOneOfExpectedTokenFromListOrThrow(
                TokenType.AS_4_ATTRIBUTE_TOKEN_TYPES
        );
        lastConsumedToken = as4AttributeToken;

        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.SEMICOLON);

        Token compareString = getOneOfExpectedTokenFromListOrThrow(TokenType.VALUE);
        lastConsumedToken = compareString;
        String valueString = compareString.value.substring(
                1,
                compareString.value.length() - 1
        ); // remove leading "'" and trailing "'"

        var matchExpression = createMatchExpression(operatorToken, as4AttributeToken, valueString);

        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return matchExpression;
    }

    private ConnectorMatchExpression createMatchExpression(
            Token operandToken, Token as4AttributeToken, String valueString) {
        if (operandToken.type == TokenType.EQUALS) {
            return new ConnectorEqualsExpression(as4AttributeToken.type, valueString);
        } else {
            return new ConnectorStartsWithExpression(as4AttributeToken.type, valueString);
        }
    }

    private Token getOneOfExpectedTokenFromListOrThrow(TokenType tokenType) {
        return getOneOfExpectedTokenFromListOrThrow(
                Stream.of(tokenType).collect(Collectors.toList()));
    }

    private Token getOneOfExpectedTokenFromListOrThrow(List<TokenType> expectedTokenTypes) {
        String expectedTokensString = expectedTokenTypes
                .stream()
                .map(TokenType::toString)
                .collect(Collectors.joining(","));

        if (tokens.isEmpty()) {
            throw new ParsingException(
                    lastConsumedToken,
                    String.format(
                            "parsing error at %d: I would expect one of these: %s",
                            this.lastConsumedToken.end + 1, expectedTokensString
                    )
            );
        }

        var token = tokens.removeFirst();

        if (isNotAToken(token, expectedTokenTypes)) {
            if (tokens.isEmpty()) {
                throw new ParsingException(
                        lastConsumedToken, token,
                        String.format(
                                "parsing error at %d: I would expect one of these: %s, but I "
                                + "got %s",
                                this.lastConsumedToken.end + 1, expectedTokensString, token
                        )
                );
            }
        }
        this.lastConsumedToken = token;

        return token;
    }

    private ConnectorExpression processBooleanToken(Token operatorToken) {
        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);
        var expression1 = createExpressions();
        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.SEMICOLON);
        var expression2 = createExpressions();
        getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return new ConnectorBinaryOperatorExpression(
                operatorToken.type, expression1, expression2);
    }

    private boolean isNotAToken(Token token, List<TokenType> t) {
        return !(isAToken(token, t)); // tv == null || tv.t != t;
    }

    private boolean isAToken(Token token, List<TokenType> tokenTypes) {
        // avoid checking token nullability because it couldn't happen
        return tokenTypes.contains(token.type);
    }

    public Optional<ConnectorExpression> getParsedConnectorExpression() {
        return Optional.ofNullable(parsedConnectorExpression);
    }

    /**
     * Represents a parsing exception that can occur during parsing of tokens.
     */
    @Getter
    public static class ParsingException extends RuntimeException {
        private Token lastConsumedToken;
        private Token currentToken = null;
        private int col = -1;

        public ParsingException(String message) {
            super(message);
        }

        /**
         * Constructs a new ParsingException with the specified last consumed token and format.
         *
         * @param lastConsumedToken the last consumed token
         * @param format            the format of the exception message
         */
        public ParsingException(Token lastConsumedToken, String format) {
            super(format);
            this.col = lastConsumedToken.start;
            this.lastConsumedToken = lastConsumedToken;
        }

        /**
         * Constructs a new ParsingException with the specified last consumed token, current token,
         * and format.
         *
         * @param lastConsumedToken the last consumed token
         * @param currentToken      the current token
         * @param format            the format of the exception message
         */
        public ParsingException(Token lastConsumedToken, Token currentToken, String format) {
            super(format);
            this.lastConsumedToken = lastConsumedToken;
            this.currentToken = currentToken;
            this.col = currentToken.start;
        }
    }
}
