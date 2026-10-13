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
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeParty;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Repository (port) for persisting and querying {@link ConnectorProcessingModeParty} objects, which
 * are scoped to a business domain.
 */
public interface ConnectorProcessingModePartyRepository {
    /**
     * Persists the given parties for the given business domain.
     *
     * @param parties                  the parties to save (must not be null)
     * @param businessDomainIdentifier the identifier of the business domain the parties belong to
     *                                 (must not be null)
     *
     * @return the persisted {@link ConnectorProcessingModeParty} objects, reflecting the state
     *     stored in the repository
     */
    List<ConnectorProcessingModeParty> saveAll(
        @Nonnull List<ConnectorProcessingModeParty> parties,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a party by its identifier, role type and business domain.
     *
     * @param identifier               the identifier of the party (must not be null or blank)
     * @param roleType                 the {@link ConnectorPartyRoleType} of the party (must not be
     *                                 null)
     * @param businessDomainIdentifier the identifier of the business domain the party belongs to
     *                                 (must not be null)
     *
     * @return the {@link ConnectorProcessingModeParty} matching the given identifier, role type and
     *     business domain, or {@code null} if no such party exists
     */
    ConnectorProcessingModeParty findByIdentifierAndRoleTypeAndBusinessDomain(
        @Nonnull String identifier,
        @Nonnull ConnectorPartyRoleType roleType,
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves all parties belonging to the given business domain.
     *
     * @param identifier the identifier of the business domain (must not be null)
     *
     * @return the {@link ConnectorProcessingModeParty} objects of the business domain; never
     *     {@code null}, and empty if there are none
     */
    List<ConnectorProcessingModeParty> findAllByBusinessDomainIdentifier(
        @Nonnull ConnectorBusinessDomainIdentifier identifier);
}
