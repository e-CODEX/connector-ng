/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeAction;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for persisting and querying {@link ConnectorProcessingModeAction} objects,
 * which are scoped to a business domain.
 */
public interface ConnectorProcessingModeActionRepository {
    /**
     * Persists the given actions for the given business domain.
     *
     * @param actions                  the actions to save (must not be null)
     * @param businessDomainIdentifier the identifier of the business domain the actions belong to
     *                                 (must not be null)
     *
     * @return the persisted {@link ConnectorProcessingModeAction} objects, reflecting the state
     *     stored in the repository
     */
    List<ConnectorProcessingModeAction> saveAll(
        @Nonnull List<ConnectorProcessingModeAction> actions,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves an action by its name and business domain.
     *
     * @param name                     the name of the action (must not be null or blank)
     * @param businessDomainIdentifier the identifier of the business domain the action belongs to
     *                                 (must not be null)
     *
     * @return the {@link ConnectorProcessingModeAction} matching the given name and business
     *     domain, or {@code null} if no such action exists
     */
    ConnectorProcessingModeAction findByNameAndBusinessDomain(
        @Nonnull String name,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves all actions belonging to the given business domain.
     *
     * @param identifier the identifier of the business domain (must not be null)
     *
     * @return the {@link ConnectorProcessingModeAction} objects of the business domain; never
     *     {@code null}, and empty if there are none
     */
    List<ConnectorProcessingModeAction> findAllByBusinessDomainIdentifier(
        @Nonnull ConnectorBusinessDomainIdentifier identifier);
}
