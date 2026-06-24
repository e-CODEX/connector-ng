/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.exception;

/**
 * Exception to signal an error during the attachment upload process in the connector.
 *
 * <p>This exception is used to represent scenarios where the upload of an attachment fails due to
 * unexpected conditions. It is typically thrown when issues arise during the processing of
 * attachment data in the connector's infrastructure.
 */
public class ConnectorAttachmentUploadException extends RuntimeException {
    public ConnectorAttachmentUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
