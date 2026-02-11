/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.keystore.ConnectorKeystore;
import jakarta.annotation.Nonnull;

/**
 * Defines the contract for managing {@link ConnectorKeystore} instances within a specific business
 * domain in the connector system.
 */
public interface ConnectorKeystoreRepository {
    /**
     * Saves the given {@link ConnectorKeystore} instance into the context of the specified
     * {@link ConnectorBusinessDomainIdentifier}.
     *
     * @param keystore                 the {@link ConnectorKeystore} instance to be saved; must not
     *                                 be null.
     * @param businessDomainIdentifier the {@link ConnectorBusinessDomainIdentifier} representing
     *                                 the business domain with which the keystore is associated;
     *                                 must not be null.
     *
     * @return the saved {@link ConnectorKeystore} instance with any modifications or updates
     *         applied during the save process.
     */
    ConnectorKeystore save(
            @Nonnull ConnectorKeystore keystore,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
