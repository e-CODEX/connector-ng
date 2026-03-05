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
 * Represents an action associated with an outbound message in the connector context.
 *
 * <p>The action typically conveys a specific instruction or intent for processing the outbound
 * message. The name of the action must be provided and cannot be blank.
 *
 * <p>Bean Validation annotations are used to ensure the validity of the provided action name.
 *
 * @param name the name of the action; must not be blank
 */
@Builder
public record ConnectorOutboundMessageAction(
        @NotBlank String name
) {
}
