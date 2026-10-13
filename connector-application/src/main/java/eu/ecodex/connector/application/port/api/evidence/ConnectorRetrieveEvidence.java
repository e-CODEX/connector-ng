/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.evidence;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Use case for retrieving the evidence associated with a connector message.
 */
public interface ConnectorRetrieveEvidence {
    /**
     * Retrieves the {@link ConnectorMessageEvidence} identified by the given UUID.
     *
     * @param uuid unique identifier of the evidence to retrieve (must not be null)
     *
     * @return the {@link ConnectorMessageEvidence} associated with the given identifier
     */
    ConnectorMessageEvidence execute(@Nonnull String uuid);
}
