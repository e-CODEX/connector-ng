/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.exception;

/**
 * Represents an exception related to issues with processing modes in the connector context. This
 * exception is used to signal errors encountered during operations involving specific processing
 * modes, providing context about the error condition.
 *
 * <p>Subclasses of this exception may further specialize error reporting for different components
 * or aspects of processing mode validation and execution.
 */
public class ConnectorProcessingModeException extends RuntimeException {
    public ConnectorProcessingModeException(String message) {
        super(message);
    }

    public ConnectorProcessingModeException(String message, Throwable cause) {
        super(message, cause);
    }
}
