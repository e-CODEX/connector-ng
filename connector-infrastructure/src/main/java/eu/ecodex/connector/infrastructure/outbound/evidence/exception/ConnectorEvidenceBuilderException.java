/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.exception;

import java.io.Serial;

/**
 * This exception is thrown when an error occurs during the process of building REM (Registered
 * Electronic Mail) evidences. It acts as a specific type of {@link Exception} to indicate issues
 * related to evidence creation within the connector infrastructure.
 *
 * <p>The exception may carry additional details about the error through its message or cause.
 */
@SuppressWarnings("squid:S1135")
public class ConnectorEvidenceBuilderException extends Exception {
    @Serial
    private static final long serialVersionUID = -8972538454498818077L;

    public ConnectorEvidenceBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorEvidenceBuilderException(String message) {
        super(message);
    }

    public ConnectorEvidenceBuilderException(Throwable cause) {
        super(cause);
    }
}
