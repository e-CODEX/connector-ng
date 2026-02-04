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
 * Represents the name of a link partner within a connector system.
 *
 * <p>This record is utilized to encapsulate the name attribute for a partner entity,
 * enabling clearer identification and management of link partners in the context of
 * connector-related operations. It serves as a lightweight data structure that ensures consistency
 * and reusability across the domain.
 *
 * @param name The name of the link partner.
 */
@Builder
public record ConnectorLinkPartnerName(
        String name
) {
    @Override
    @Nonnull public String toString() {
        return String.format("{name=%s}", name);
    }
}
