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
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Defines the contract for managing and querying parties within a specific business domain in the
 * connector system.
 */
public interface ConnectorPartyRepository {
    List<ConnectorParty> saveAll(
            @Nonnull List<ConnectorParty> parties,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorParty} based on the specified party and associated business
     * domain identifier.
     *
     * @param name                     the {@link ConnectorParty} representing the party; must not
     *                                 be null.
     * @param businessDomainIdentifier the {@link ConnectorBusinessDomainIdentifier} representing
     *                                 the business domain associated with the party; must not be
     *                                 null.
     *
     * @return the {@link ConnectorParty} matching the specified party and business domain
     *         identifier, or null if no such party exists.
     */
    // TODO to be removed
    ConnectorParty findByNameAndBusinessDomain(
            String name, ConnectorBusinessDomainIdentifier businessDomainIdentifier);

    /**
     * Retrieves a {@link ConnectorParty} based on the specified identifier, role type, and
     * associated business domain identifier.
     *
     * @param identifier               the unique identifier of the party; must not be null or
     *                                 blank.
     * @param roleType                 the {@link ConnectorPartyRoleType} representing the role type
     *                                 of the party; must not be null.
     * @param businessDomainIdentifier the {@link ConnectorBusinessDomainIdentifier} representing
     *                                 the business domain associated with the party; must not be
     *                                 null.
     *
     * @return the {@link ConnectorParty} matching the specified identifier, role type, and business
     *         domain identifier, or null if no such party exists.
     */
    ConnectorParty findByIdentifierAndRoleTypeAndBusinessDomain(
            @Nonnull String identifier,
            @Nonnull ConnectorPartyRoleType roleType,
            @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier);
}
