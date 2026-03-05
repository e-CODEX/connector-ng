/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Represents a party associated with an outbound message request.
 *
 * @param identifier     the unique identifier of the party; must not be empty
 * @param identifierType the type of the identifier; must not be empty
 * @param role           the role of the party in the message; must not be empty
 */
@Builder
public record ConnectorOutboundMessageParty(
        @NotBlank(message = "Identifier must not be empty")
        String identifier,
        @NotBlank(message = "Identifier type must not be empty")
        String identifierType,
        @NotBlank(message = "Role must not be empty")
        String role
) {
}
