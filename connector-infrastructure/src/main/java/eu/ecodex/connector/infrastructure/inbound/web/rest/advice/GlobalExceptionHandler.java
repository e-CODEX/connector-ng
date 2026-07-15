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

import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.domain.exception.ConnectorBusinessDomainNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceException;
import eu.ecodex.connector.domain.exception.ConnectorEvidenceNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorMessageAttachmentException;
import eu.ecodex.connector.domain.exception.ConnectorMessageNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorMessageTransportStepNotFoundException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeException;
import eu.ecodex.connector.domain.exception.ConnectorProcessingModeNotFoundException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorAttachmentUploadException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorBadRequestException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorInternalServerException;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions thrown across the application. It provides
 * centralized exception handling using Spring's @RestControllerAdvice and @ExceptionHandler
 * annotations.
 */
@Hidden
@RestControllerAdvice
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConnectorBusinessDomainAlreadyExistsException.class)
    public ErrorResponse handleBusinessDomainException(
            ConnectorBusinessDomainAlreadyExistsException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConnectorBusinessDomainNotFoundException.class)
    public ErrorResponse handleBusinessDomainNotFoundException(
            ConnectorBusinessDomainNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConnectorProcessingModeNotFoundException.class)
    public ErrorResponse handleProcessingModeNotFoundException(
            ConnectorProcessingModeNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConnectorMessageNotFoundException.class)
    public ErrorResponse handleMessageNotFoundException(
            ConnectorMessageNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConnectorMessageTransportStepNotFoundException.class)
    public ErrorResponse handleMessageTransportStepsNotFoundException(
            ConnectorMessageTransportStepNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConnectorEvidenceNotFoundException.class)
    public ErrorResponse handleMessageEvidenceNotFoundException(
            ConnectorEvidenceNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConnectorProcessingModeException.class)
    public ErrorResponse handleProcessingModeException(ConnectorProcessingModeException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConnectorEvidenceException.class)
    public ErrorResponse handleConnectorEvidenceException(ConnectorEvidenceException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConnectorBadRequestException.class)
    public ErrorResponse handleBadRequestException(ConnectorBadRequestException exception) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConnectorMessageAttachmentException.class)
    public ErrorResponse handleMessageAttachmentException(
            ConnectorMessageAttachmentException exception) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConnectorAttachmentUploadException.class)
    public ErrorResponse handleAttachmentUploadException(ConnectorAttachmentUploadException e) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConnectorInternalServerException.class)
    public ErrorResponse handleInternalServerException(ConnectorInternalServerException exception) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                                  .getAllErrors()
                                  .stream()
                                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                  .collect(Collectors.joining(", "));
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }
}
