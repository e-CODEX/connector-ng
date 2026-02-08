/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.presentation.rest.advice;

import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions thrown across the application. It provides
 * centralized exception handling using Spring's @RestControllerAdvice and @ExceptionHandler
 * annotations.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles exceptions of type {@link ConnectorBusinessDomainException} that indicate business
     * domain-specific rule violations or issues within the connector context.
     *
     * @param exception the {@code ConnectorBusinessDomainException} that needs to be handled
     *
     * @return an {@code ErrorResponse} containing the HTTP status code and exception details
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConnectorBusinessDomainException.class)
    public ErrorResponse handleBusinessDomainException(ConnectorBusinessDomainException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage()
        );
    }
}
