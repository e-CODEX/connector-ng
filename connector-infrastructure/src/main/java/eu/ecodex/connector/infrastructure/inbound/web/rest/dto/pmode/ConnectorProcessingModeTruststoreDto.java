/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode;

import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.domain.model.security.KeystoreType;

/**
 * Represents a Data Transfer Object (DTO) for truststore information used in connector processing
 * mode configuration.
 *
 * @param filename The name of the truststore file.
 * @param password The password associated with the truststore for secure access.
 * @param type     The type of the truststore, represented by {@link KeystoreType}.
 */
public record ConnectorProcessingModeTruststoreDto(
    String filename,
    String password,
    KeystoreType type
) {
    /**
     * Constructs a {@link ConnectorProcessingModeTruststoreDto} instance from the provided
     * {@link ConnectorTruststore}.
     *
     * @param truststore the {@link ConnectorTruststore} instance containing the truststore
     *                   configuration data
     *
     * @return a {@link ConnectorProcessingModeTruststoreDto} instance created using the data from
     *     the provided {@link ConnectorTruststore}
     */
    public static ConnectorProcessingModeTruststoreDto from(ConnectorTruststore truststore) {
        if (truststore == null) {
            return null;
        }

        return new ConnectorProcessingModeTruststoreDto(
            truststore.filename(),
            truststore.password(),
            truststore.type()
        );
    }
}
