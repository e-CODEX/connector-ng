/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.controller.rest.exception;

/**
 * Exception to signal an internal server error related to connector processing.
 *
 * <p>This exception is used to indicate server-side errors that occur during connector operations
 * when the system is unable to complete a request due to an unexpected issue within the internal
 * processing layer.
 *
 * <p>The exception extends {@link RuntimeException}, making it suitable for scenarios where
 * unchecked errors need to be represented.
 */
public class ConnectorInternalServerException extends RuntimeException {
    public ConnectorInternalServerException(String message) {
        super(message);
    }
}
