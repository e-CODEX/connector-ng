/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Defines the contract for managing and querying actions associated with a specific business domain
 * and action name within the connector system.
 */
public interface ConnectorActionRepository {
    List<ConnectorAction> saveAll(
            @Nonnull List<ConnectorAction> actions,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorAction} based on its name and associated business domain
     * identifier.
     *
     * @param name                     the name of the action; must not be null or blank.
     * @param businessDomainIdentifier the identifier representing the business domain with which
     *                                 the action is associated; must not be null.
     *
     * @return the {@link ConnectorAction} matching the specified action name and business domain
     *         identifier, or null if no such action exists.
     */
    ConnectorAction findByNameAndBusinessDomain(
            @Nonnull String name,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
