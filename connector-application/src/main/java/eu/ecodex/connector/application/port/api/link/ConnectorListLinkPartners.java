/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.link;

import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import java.util.List;

/**
 * Use case for listing the {@link ConnectorLinkPartner} entities of a given link type.
 */
public interface ConnectorListLinkPartners {
    /**
     * Retrieves the link partners configured for the given link type.
     *
     * @param linkType the type of link ({@link ConnectorLinkType}) whose partners are listed; may
     *                 be {@code null} to list the partners of all link types
     *
     * @return a list of {@link ConnectorLinkPartner} objects of the given link type; never
     *     {@code null}, and empty if no link partners are configured
     */
    List<ConnectorLinkPartner> execute(ConnectorLinkType linkType);
}
