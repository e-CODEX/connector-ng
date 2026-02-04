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

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a token used for processing and evaluating expressions.
 *
 * <p>A token is defined by its type, value, and positional information
 * in the original input. It is used for parsing and interpreting text based on predefined token
 * types.
 *
 * <p>This class provides the necessary structure to enable tokenization of complex expressions
 * and facilitates later evaluation or interpretation of those tokens.
 *
 * <p>Fields:
 * <ul>
 *     <li> tokenType: The type of the token, defined by the {@link TokenType} enum.
 *     <li> value: The actual text value that the token represents.
 *     <li> start: The starting position of the token in the input text.
 *     <li> end: The ending position of the token in the input text.
 * </ul>
 *
 * <p>Usage:
 * Tokens are typically used in lexical analysis, parsing, or interpreting structured input. The
 * {@link TokenType} enum defines the possible types of tokens, which could correspond to operators,
 * keywords, literals, or other structural elements.
 */
@Data
@NoArgsConstructor
public class Token implements Serializable {
    TokenType type;
    String value;
    int start;
    int end;
}
