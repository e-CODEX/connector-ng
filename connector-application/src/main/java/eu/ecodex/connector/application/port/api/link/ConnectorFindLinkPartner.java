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

import eu.ecodex.connector.application.exception.ConnectorLinkPartnerException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import jakarta.annotation.Nonnull;

/**
 * Service interface for retrieving {@link ConnectorLinkPartner} instances.
 *
 * <p>Implementations are responsible for locating a connector link partner based on certificate
 * information used during secure communication.
 */
public interface ConnectorFindLinkPartner {
    /**
     * Finds a {@link ConnectorLinkPartner} associated with the given certificate distinguished name
     * (DN).
     *
     * @param certificateDn the distinguished name extracted from a client certificate
     *
     * @return the matching {@link ConnectorLinkPartner}
     *
     * @throws ConnectorLinkPartnerException if no partner matches the provided DN
     */
    ConnectorLinkPartner findByCertificateDn(@Nonnull String certificateDn);
}
