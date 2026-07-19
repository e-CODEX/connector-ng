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
 * Repository interface for accessing {@link ConnectorLinkPartner} instances.
 *
 * <p>This interface defines the contract for retrieving ConnectorLinkPartners by their unique
 * {@link ConnectorLinkPartnerName}. Implementations might use different storage mechanisms such as
 * databases, in-memory collections.
 */
public interface ConnectorLinkPartnerRepository {
    ConnectorLinkPartner findByName(@Nonnull ConnectorLinkPartnerName name);

    ConnectorLinkPartner findByCertificateDn(@Nonnull String certificateDn);

    List<ConnectorLinkPartner> findAll();
}
