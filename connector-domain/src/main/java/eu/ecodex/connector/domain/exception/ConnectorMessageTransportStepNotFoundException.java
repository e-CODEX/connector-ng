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
 * Exception thrown when a specific transport step in a connector message cannot be found.
 *
 * <p>This is a specialized form of {@link NotFoundException}, used to indicate
 * that the requested transport step associated with a connector message is missing or cannot be
 * located within the context of the connector's operations.
 */
public class ConnectorMessageTransportStepNotFoundException extends NotFoundException {
    public ConnectorMessageTransportStepNotFoundException(String message) {
        super(message);
    }
}
