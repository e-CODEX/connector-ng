/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.link;

import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartnerName;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Provider (port) giving access to {@link ConnectorLinkPartner} instances.
 *
 * <p>This interface defines the contract for retrieving link partners by their unique
 * {@link ConnectorLinkPartnerName} or by the distinguished name (DN) of their certificate.
 * Implementations may use different sources, such as a database, configuration properties or an
 * in-memory collection.
 */
public interface ConnectorLinkPartnerProvider {

    /**
     * Finds a link partner by its unique name.
     *
     * @param name the unique name of the link partner (must not be null)
     *
     * @return the matching {@link ConnectorLinkPartner}, or {@code null} if none exists
     */
    ConnectorLinkPartner findByName(@Nonnull ConnectorLinkPartnerName name);

    /**
     * Finds a link partner by the distinguished name (DN) of its certificate.
     *
     * @param certificateDn the distinguished name of the partner's certificate, for example
     *                      {@code CN=partner,O=Example,C=FR} (must not be null)
     *
     * @return the matching {@link ConnectorLinkPartner}, or {@code null} if none exists
     */
    ConnectorLinkPartner findByCertificateDn(@Nonnull String certificateDn);

    /**
     * Retrieves all known link partners.
     *
     * @return all {@link ConnectorLinkPartner} objects; never {@code null}, and empty if none are
     *     configured
     */
    List<ConnectorLinkPartner> findAll();
}
