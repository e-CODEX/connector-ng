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
 * Represents an exception thrown when processing mode verification fails within the connector
 * context. This exception is a specialized form of {@link ConnectorProcessingModeException},
 * providing an explicit sign of errors related to verifying the processing mode configuration
 * or logic.
 *
 * <p>This exception may be used to signal issues such as invalid or incompatible
 * processing modes, enabling more precise error identification and handling.
 */
public class ConnectorProcessingModeVerificationException extends ConnectorProcessingModeException {
    public ConnectorProcessingModeVerificationException(String message) {
        super(message);
    }

    public ConnectorProcessingModeVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
