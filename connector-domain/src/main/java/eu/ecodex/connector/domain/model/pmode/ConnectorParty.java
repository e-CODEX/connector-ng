/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.pmode;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;

/**
 * The Party represents a party in the Connector. It contains information about the party's ID, ID
 * type, and role.
 *
 * @param name           The name of the party.
 * @param identifier     The ID of the party.
 * @param identifierType The type of the ID of the party.
 * @param role           The role of the party.
 * @param roleType       The type of the role of the party.
 * @param isHome         Whether the party is the home party of the connector.
 */
@Builder(toBuilder = true)
public record ConnectorParty(
        @Nullable String name,
        @NotBlank String identifier,
        @Nonnull String identifierType,
        @NotBlank String role,
        @Nonnull ConnectorPartyRoleType roleType,
        boolean isHome
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{name=%s, uuid=%s, identifierType=%s, role=%s, roleType=%s, isHome=%s}",
                name, identifier, identifierType, role, roleType, isHome
        );
    }
}
