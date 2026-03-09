/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.service;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import jakarta.annotation.Nonnull;

/**
 * Interface for managing evidences related to messages in the connector domain. The service
 * provides methods to create and process evidence of specific types for given messages.
 */
public interface ConnectorEvidenceService {
    /**
     * Determines whether evidence triggering is allowed for the given connector message. This
     * method evaluates the provided message to decide if evidence creation or associated operations
     * can be initiated.
     *
     * @param message The connector message to evaluate. Must not be null.
     */
    void isEvidenceTriggeringAllowed(@Nonnull ConnectorMessage message);
}
