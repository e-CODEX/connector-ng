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
 * Represents an exception thrown when a specific message in the connector context cannot be found.
 * This is a specialized form of {@link NotFoundException}, intended to handle cases where the
 * requested processing mode is unavailable.
 */
public class ConnectorProcessingModeNotFoundException extends NotFoundException {
    public ConnectorProcessingModeNotFoundException(String message) {
        super(message);
    }
}
