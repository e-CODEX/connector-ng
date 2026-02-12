/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.exception;

/**
 * Exception to signal a bad request error related to the connector processing.
 *
 * <p>This exception is used to represent client-side errors, typically caused by invalid or
 * missing input data sent to the server. It results in a response with an appropriate error message
 * indicating the nature of the bad request.
 *
 * <p>The exception extends {@link RuntimeException}, allowing it to be used for unchecked error
 * scenarios in the application.
 */
public class ConnectorBadRequestException extends RuntimeException {
    public ConnectorBadRequestException(String message) {
        super(message);
    }
}
