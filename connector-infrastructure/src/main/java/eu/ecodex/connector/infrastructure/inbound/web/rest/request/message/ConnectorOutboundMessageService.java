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
 * Represents a service associated with an outbound message request.
 *
 * @param name the name of the service; must not be blank
 * @param type the type of the service; must not be blank
 */
@Builder
public record ConnectorOutboundMessageService(
    @NotBlank String name,
    @NotBlank String type
) {
}
