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

import eu.ecodex.connector.domain.model.security.ConnectorTruststore;

/**
 * Use case for updating the truststore associated with a processing mode.
 */
public interface ConnectorUpdateProcessingModeTruststore {
    /**
     * Updates the {@link ConnectorTruststore} of the processing mode identified by the given UUID.
     *
     * @param pmodeUuid  unique identifier of the processing mode whose truststore is updated (must
     *                   not be null)
     * @param truststore the new truststore to associate with the processing mode (must not be
     *                   null)
     *
     * @return the updated {@link ConnectorTruststore}, as persisted
     */
    ConnectorTruststore execute(String pmodeUuid, ConnectorTruststore truststore);
}
