/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.pmode;

import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import jakarta.annotation.Nonnull;

/**
 * This interface defines the contract for retrieving and processing connector messages based on a
 * specified identifier.
 */
public interface ConnectorRetrieveProcessingMode {
    /**
     * Executes the retrieval of a connector message based on the provided identifier.
     *
     * @param uuid the unique identifier used to locate and retrieve the specific connector
     *             processing mode
     *
     * @return the retrieved {@link ConnectorProcessingMode} associated with the given identifier
     */
    ConnectorProcessingMode execute(@Nonnull String uuid);
}
