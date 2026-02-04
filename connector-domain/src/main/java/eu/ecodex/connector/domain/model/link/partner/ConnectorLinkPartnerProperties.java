/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.link.partner;

import jakarta.annotation.Nonnull;
import lombok.Builder;


/**
 * Represents the properties of a link partner in the connector system.
 *
 * <p>This record is used to define key attributes necessary for establishing and maintaining
 * communication with a link partner. These properties include the partner's endpoint address,
 * encryption details, and certificate information.
 *
 * @param pushAddress     The URL or address where outgoing messages should be sent for this link
 *                        partner.
 * @param encryptionAlias The alias identifying the encryption details or keys used for secure
 *                        communication with the partner.
 * @param certificateDn   The distinguished name (DN) of the certificate associated with the link
 *                        partner, ensuring authenticity and security during interactions.
 */
@Builder
public record ConnectorLinkPartnerProperties(
        String pushAddress,
        String encryptionAlias,
        String certificateDn
) {
    @Override
    public @Nonnull String toString() {
        return String.format(
                "{pushAddress=%s, encryptionAlias=%s, certificateDn=%s}",
                pushAddress, encryptionAlias, certificateDn
        );
    }
}
