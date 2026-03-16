/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.link;

import eu.ecodex.connector.domain.exception.ConnectorLinkPartnerException;
import eu.ecodex.connector.domain.model.link.partner.ConnectorLinkPartner;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Interface for verifying connector messages specifically in the context of a
 * {@link ConnectorLinkPartner}.
 *
 * <p>Implementations are responsible for validating that a message originates from a trusted
 * partner and complies with the partner-specific configuration requirements.
 */
public interface ConnectorLinkPartnerVerifier {
    /**
     * Verifies the given {@link ConnectorMessage} against partner rules.
     *
     * @param message the message to verify; must not be {@code null}
     *
     * @throws ConnectorLinkPartnerException if verification fails or the message is invalid for the
     *                                       partner
     */
    void verify(@Nonnull ConnectorMessage message);
}
