/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.advice;

/**
 * Represents a generic error response for REST API endpoints.
 *
 * <p>This record is used to encapsulate error details, providing an HTTP status code and
 * a descriptive message. It is typically used in scenarios where the server needs to inform the
 * client about application-specific errors or violations, such as invalid input, business rule
 * breaches, or other runtime exceptions.
 *
 * <p>Instances of this record are returned by all exception handlers or API operations
 * when an error response is required.
 *
 * @param status  The HTTP status code representing the type of error encountered.
 * @param message A descriptive error message detailing the cause of the error.
 */
public record ErrorResponse(
        int status,
        String message
) {
}
