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
import lombok.Getter;

/**
 * Represents a binary operator expression.
 */
@Getter
public class ConnectorBinaryOperatorExpression extends ConnectorExpression {
    private final TokenType operand;
    private final ConnectorExpression exp1;
    private final ConnectorExpression exp2;

    /**
     * Represents a binary operator expression.
     *
     * @param tokenType   The token type of the binary operator.
     * @param expression1 The first expression operand.
     * @param expression2 The second expression operand.
     */
    public ConnectorBinaryOperatorExpression(
            TokenType tokenType, ConnectorExpression expression1, ConnectorExpression expression2) {
        this.operand = tokenType;
        this.exp1 = expression1;
        this.exp2 = expression2;
    }

    @Override
    boolean evaluate(ConnectorMessage message) {
        return switch (operand) {
            case TokenType.OR -> exp1.evaluate(message) || exp2.evaluate(message);
            case TokenType.AND -> exp1.evaluate(message) && exp2.evaluate(message);
            default -> throw new RuntimeException(String.format("unsupported OPERAND %s", operand));
        };
    }
}
