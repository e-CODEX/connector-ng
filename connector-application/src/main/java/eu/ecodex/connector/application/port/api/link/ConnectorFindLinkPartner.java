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

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import jakarta.annotation.Nonnull;

/**
 * Use case for finding a {@link ConnectorLinkPartner}.
 */
public interface ConnectorFindLinkPartner {
    /**
     * Finds the {@link ConnectorLinkPartner} whose certificate matches the given distinguished
     * name (DN).
     *
     * @param certificateDn distinguished name (DN) of the partner's certificate, for example
     *                      {@code CN=partner,O=Example,C=FR} (must not be null)
     *
     * @return the {@link ConnectorLinkPartner} associated with the given certificate DN
     */
    ConnectorLinkPartner findByCertificateDn(@Nonnull String certificateDn);
}
