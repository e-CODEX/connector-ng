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
 * Represents a specific exception indicating that a message in the connector context is not
 * classified as a business message. This exception is used to handle cases where operations related
 * to business messages encounter unexpected non-business message businessContent.
 *
 * <p>As a subclass of {@link ConnectorMessageException}, this exception provides a more
 * specific context for message-related errors associated with business classification.
 *
 * <p>It can be used to facilitate precise identification and handling of scenarios
 * where the message type does not meet the expected business message criteria.
 */
public class ConnectorMessageNotBusinessException extends ConnectorMessageException {
    public ConnectorMessageNotBusinessException(String message) {
        super(message);
    }
}
