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
 * Represents a negation expression.
 */
@Getter
public class ConnectorNotExpression extends ConnectorExpression {
    private final ConnectorExpression expression;
    private final Token token;

    /**
     * Constructs an instance of ConnectorNotExpression, representing a negation of the given
     * expression.
     *
     * @param expression the ConnectorExpression to be negated
     * @param token      the Token associated with this negation expression
     */
    public ConnectorNotExpression(ConnectorExpression expression, Token token) {
        this.expression = expression;
        this.token = token;
    }

    @Override
    boolean evaluate(ConnectorMessage message) {
        return !expression.evaluate(message);
    }
}
