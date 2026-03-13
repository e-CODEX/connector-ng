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
 * Exception thrown when an error occurs related to a Connector Link partner.
 *
 * <p>This exception is typically used to indicate configuration, validation, or processing issues
 * involving a connector link partner.
 */
public class ConnectorLinkPartnerException extends RuntimeException {
    public ConnectorLinkPartnerException(String message) {
        super(message);
    }
}
