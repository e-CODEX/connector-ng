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

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Represents a service associated with an outbound message request.
 *
 * @param name the name of the service (must not be blank)
 * @param type the type of the service (must not be blank)
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ConnectorOutboundMessageService {
    @NotBlank(message = "The [name] must not be blank")
    String name;

    @NotBlank(message = "The [type] must not be blank")
    String type;
}
