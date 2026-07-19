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
 * Represents an exception thrown when there are issues specifically related to the party
 * information within a connector message. This exception is a specialized form of
 * {@link ConnectorMessageException}, allowing for more precise identification and handling of
 * errors associated with message party operations or data.
 */
public class ConnectorMessagePartyException extends ConnectorMessageException {
    public ConnectorMessagePartyException(String message) {
        super(message);
    }
}
