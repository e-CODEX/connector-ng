/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.c2ctest;

import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.outbound.ConnectorOutboundMessageParty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Represents AS4-specific properties associated with an outbound test message request.
 *
 * <p>This record encapsulates the metadata necessary to construct the AS4 message header and
 * routing information, including sender and receiver details and conversation tracking
 * identifiers.
 *
 * @param ebmsIdentifier         the EBMS identifier set by the backend system; ignored if the
 *                               connector is configured to generate EBMS identifiers; may be
 *                               {@code null}
 * @param conversationIdentifier the identifier used to correlate messages belonging to the same
 *                               business conversation; may be {@code null} if conversation tracking
 *                               is not required
 * @param originalSender         the original sender identifier, as defined by the AS4 messaging
 *                               specification (must not be blank)
 * @param finalRecipient         the final recipient identifier, as defined by the AS4 messaging
 *                               specification (must not be blank)
 * @param fromParty              the {@link ConnectorOutboundMessageParty} representing the sending
 *                               party in the AS4 exchange (must not be null)
 * @param toParty                the {@link ConnectorOutboundMessageParty} representing the
 *                               receiving party in the AS4 exchange (must not be null)
 */
@Builder(toBuilder = true)
public record ConnectorTestMessageAS4Properties(
    String ebmsIdentifier,

    String conversationIdentifier,

    @NotBlank(message = "The [originalSender] must not be blank.")
    @Size(max = 255, message = "The [originalSender] must not exceed 255")
    String originalSender,

    @NotBlank(message = "The [finalRecipient] must not be blank.")
    @Size(max = 255, message = "The [finalRecipient] must not exceed 255")
    String finalRecipient,

    @Valid
    @NotNull(message = "The [fromParty] must not be null.")
    ConnectorOutboundMessageParty fromParty,

    @Valid
    @NotNull(message = "The [toParty] must not be null.")
    ConnectorOutboundMessageParty toParty
) {
}
