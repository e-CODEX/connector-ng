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
 * Represents an expression.
 */
public abstract class ConnectorExpression {
    /**
     * Evaluates the provided connector message against a specific condition or set of criteria.
     *
     * @param message the {@code ConnectorMessage} instance to evaluate. It provides context for the
     *                evaluation and determines whether the condition is met.
     *
     * @return {@code true} if the {@code ConnectorMessage} meets the evaluation criteria;
     *     {@code false} otherwise.
     */
    abstract boolean evaluate(ConnectorMessage message);
}
