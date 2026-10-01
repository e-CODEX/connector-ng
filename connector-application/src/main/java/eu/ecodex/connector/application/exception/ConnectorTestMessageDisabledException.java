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
 * Thrown when an operation involving a connector test message is attempted while test message
 * handling is disabled in the connector configuration.
 */
public class ConnectorTestMessageDisabledException extends RuntimeException {
    public ConnectorTestMessageDisabledException(String message) {
        super(message);
    }
}
