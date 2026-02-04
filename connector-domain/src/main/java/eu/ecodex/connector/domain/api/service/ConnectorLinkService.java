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

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import jakarta.annotation.Nonnull;

/**
 * Interface for managing operations on connector link partners within the system.
 *
 * <p>This service provides methods to add and retrieve link partners, enabling seamless
 * management of connector-related integration links. The link partners represent configurations and
 * metadata associated with external or internal systems interacting with the connector.
 *
 * <p>The primary responsibility of this interface is to encapsulate interaction with
 * {@link ConnectorLinkPartner} entities and their associated identifiers.
 */
public interface ConnectorLinkService {
    /**
     * Adds a new connector link partner to the system.
     *
     * @param linkPartner the {@link ConnectorLinkPartner} to be added. Must not be {@code null}.
     */
    void add(@Nonnull ConnectorLinkPartner linkPartner);

    /**
     * Retrieves a {@link ConnectorLinkPartner} using the specified link partner name.
     *
     * @param linkPartnerName the {@link ConnectorLinkPartnerName} representing the name of the link
     *                        partner to search for. Must not be null.
     *
     * @return the {@link ConnectorLinkPartner} associated with the given name, or {@code null} if
     *         no matching link partner is found.
     */
    ConnectorLinkPartner getByLinkPartnerName(@Nonnull ConnectorLinkPartnerName linkPartnerName);
}
