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

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for managing and retrieving {@link ConnectorAction} entities.
 */
public interface ConnectorActionService {
    List<ConnectorAction> persistAll(
            @Nonnull List<ConnectorAction> actions,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorAction} by its name and associated business domain uuid.
     *
     * @param actionName               the name of the action to search for.
     * @param businessDomainIdentifier the uuid of the business domain in which the action resides.
     *
     * @return the {@link ConnectorAction} matching the specified name and business domain uuid, or
     *         {@code null} if no such action exists.
     */
    ConnectorAction findByNameAndBusinessDomain(
            String actionName, ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
