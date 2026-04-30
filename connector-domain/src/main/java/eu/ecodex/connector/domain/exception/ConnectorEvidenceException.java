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
 * Represents a base exception for errors related to evidence processing in the connector context.
 */
public class ConnectorEvidenceException extends RuntimeException {
    public ConnectorEvidenceException(String message) {
        super(message);
    }

    public ConnectorEvidenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
