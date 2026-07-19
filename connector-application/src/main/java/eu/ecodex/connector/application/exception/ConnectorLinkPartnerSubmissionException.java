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
 * Represents an exception thrown when an error occurs during the submission process involving a
 * link partner in the connector context. This exception extends {@code RuntimeException}, allowing
 * for unchecked exceptions during runtime.
 *
 * <p>This exception can be used to encapsulate errors specific to link partner submission
 * operations, providing a descriptive message and optionally a root cause for better debugging and
 * handling.
 */
public class ConnectorLinkPartnerSubmissionException extends RuntimeException {
    public ConnectorLinkPartnerSubmissionException(String message) {
        super(message);
    }

    public ConnectorLinkPartnerSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
