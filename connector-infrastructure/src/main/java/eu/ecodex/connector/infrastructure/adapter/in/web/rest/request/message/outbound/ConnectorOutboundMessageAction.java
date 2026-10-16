/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.message.outbound;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;

/**
 * Represents an action associated with an outbound message in the connector context.
 *
 * <p>The action conveys a specific instruction or intent for processing the outbound message
 * (in the eDelivery/AS4 sense). Its name must be provided and cannot be blank.
 *
 * @param name the name of the action (must not be blank)
 */
@Builder
public record ConnectorOutboundMessageAction(
    @NotBlank(message = "The [name] must not be blank")
    String name
) implements Serializable {
}
