/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.exception.message;

/**
 * Exception thrown when a link partner client attempts to update a connector message transport step
 * that it does not own.
 */
public class ConnectorLinkPartnerNotOwnerException extends RuntimeException {
    public ConnectorLinkPartnerNotOwnerException(String message) {
        super(message);
    }
}
