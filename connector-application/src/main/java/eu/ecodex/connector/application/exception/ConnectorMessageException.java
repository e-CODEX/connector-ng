/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.exception;

/**
 * Represents a base exception for message-related errors in the connector context. This exception
 * serves as a parent exception for more specific message-related exceptions, allowing for
 * consistent categorization and handling of such errors.
 *
 * <p>Subclasses of this exception may provide more detailed context or categorization for
 * particular message-related issues encountered during the connector's operations.
 */
public class ConnectorMessageException extends RuntimeException {
    public ConnectorMessageException(String message) {
        super(message);
    }
}
