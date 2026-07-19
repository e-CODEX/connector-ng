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

import eu.ecodex.connector.domain.model.ConnectorErrorCode;

/**
 * Represents an exception that indicates evidence is not considered relevant in the context of the
 * connector's evidence processing. This exception is thrown to signal that the processing of
 * certain evidence should be ignored based on specific conditions or business rules.
 *
 * <p>It extends the {@link ConnectorEvidenceException} to provide a more specific categorization
 * for scenarios where evidence relevance issues arise.
 */
public class ConnectorEvidenceNotRelevantException extends ConnectorEvidenceException {
    public ConnectorEvidenceNotRelevantException(ConnectorErrorCode errorCode) {
        super(errorCode.toString());
    }
}
