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
 * Represents an exception thrown during the transport step of a connector message's lifecycle.
 *
 * <p>This exception is a specific subclass of {@link ConnectorMessageException}, indicating errors
 * that occur specifically while the message is being transported between systems or services.
 * It provides a way to capture and handle transport-related issues separately from other
 * message-related exceptions.
 */
public class ConnectorMessageTransportStepException extends ConnectorMessageException {
    public ConnectorMessageTransportStepException(String message) {
        super(message);
    }
}
