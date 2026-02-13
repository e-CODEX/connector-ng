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
 * Represents a business domain-specific exception within the connector context. This exception is
 * intended to handle error scenarios that occur due to violations or issues specific to the
 * business domain unicity rules of the connector.
 */
public class ConnectorBusinessDomainAlreadyExistsException extends RuntimeException {
    public ConnectorBusinessDomainAlreadyExistsException(String message) {
        super(message);
    }
}
