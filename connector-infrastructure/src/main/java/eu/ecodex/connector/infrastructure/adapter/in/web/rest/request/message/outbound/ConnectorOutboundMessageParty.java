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
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Represents a party (sender or receiver) associated with an outbound message request.
 *
 * @param identifier     the unique identifier of the party (must not be blank)
 * @param identifierType the type (scheme) of the identifier (must not be blank)
 * @param role           the role of the party in the message (must not be blank)
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ConnectorOutboundMessageParty implements Serializable {
    @NotBlank(message = "The [identifier] must not be blank")
    String identifier;

    @NotBlank(message = "The [identifierType] type must not be blank")
    String identifierType;

    @NotBlank(message = "The [role] must not be blank")
    String role;
}
