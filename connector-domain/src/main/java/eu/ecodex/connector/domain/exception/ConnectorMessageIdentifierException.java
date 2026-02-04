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
 * Represents an exception thrown when an issue occurs related to the identification of a message in
 * the context of the connector. This exception provides specific error details concerning message
 * identification failures, ensuring that such errors can be clearly distinguished and handled
 * appropriately during processing.
 */
public class ConnectorMessageIdentifierException extends ConnectorMessageException {
    public ConnectorMessageIdentifierException(String message) {
        super(message);
    }
}
