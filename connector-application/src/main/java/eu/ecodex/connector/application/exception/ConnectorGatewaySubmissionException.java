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
 * Represents an exception thrown when an error occurs during the gateway submission process in the
 * connector context. This exception extends {@code ConnectorLinkPartnerSubmissionException},
 * allowing for more specific categorization and handling of gateway submission-related errors.
 *
 * <p>This exception provides additional contextual information by supporting both a descriptive
 * message and a {@code Throwable} as the cause of the error.
 */
public class ConnectorGatewaySubmissionException extends ConnectorLinkPartnerSubmissionException {
    public ConnectorGatewaySubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
