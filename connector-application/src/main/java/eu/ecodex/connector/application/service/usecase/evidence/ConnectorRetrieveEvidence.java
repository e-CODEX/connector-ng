/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.evidence;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;

/**
 * Service interface for retrieving evidence messages.
 */
public interface ConnectorRetrieveEvidence {
    /**
     * Executes the operation to retrieve evidence for a given message identified by its UUID.
     *
     * @param uuid the unique identifier of the message evidence to be retrieved must not be null
     *
     * @return the {@link ConnectorMessageEvidence} corresponding to the specified UUID
     */
    ConnectorMessageEvidence execute(@Nonnull String uuid);
}
