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
 * Represents an exception thrown when a business domain associated with the connector
 * is not found. This exception is a specialized form of {@link NotFoundException},
 * providing a clearer context for scenarios where the absence of a business domain
 * prevents the completion of an operation.
 */
public class ConnectorBusinessDomainNotFoundException extends NotFoundException {
    public ConnectorBusinessDomainNotFoundException(String message) {
        super(message);
    }
}
