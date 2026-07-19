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

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Represents AS4-specific properties associated with an outbound message request.
 *
 * <p>This record encapsulates all metadata necessary to construct the AS4
 * message header and routing information, including sender/receiver details, service and action
 * definitions, and conversation tracking identifiers.</p>
 *
 * @param ebmsIdentifier         the EBMS identifier set by the backend system; in case the
 *                               connector is configured to generate EBMS identifiers, this value is
 *                               ignored
 * @param conversationIdentifier the unique identifier used to correlate messages belonging to the
 *                               same business conversation; may be {@code null} if conversation
 *                               tracking is not required
 * @param originalSender         the original sender identifier as defined by the AS4 messaging
 *                               specification; must not be blank
 * @param finalRecipient         the final recipient identifier, as defined by the AS4 messaging
 *                               specification, must not be blank
 * @param service                the {@link  ConnectorService} defining the business service context
 *                               of the message; must not be {@code null}
 * @param action                 the {@link ConnectorAction} defining the business action to be
 *                               executed; must not be {@code null}
 * @param fromParty              the {@link ConnectorParty} representing the sending party in the
 *                               AS4 exchange; must not be {@code null}
 * @param toParty                the {@link ConnectorParty} representing the receiving party in the
 *                               AS4 exchange; must not be {@code null}
 */
@Builder(toBuilder = true)
public record ConnectorOutboundMessageAS4Properties(
    String ebmsIdentifier,
    String conversationIdentifier,
    @NotBlank(message = "The original sender must not be blank.")
    String originalSender,
    @NotBlank(message = "The final recipient must not be blank.")
    String finalRecipient,
    @Valid
    @NotNull(message = "The service must not be null.")
    ConnectorOutboundMessageService service,
    @Valid
    @NotNull(message = "The action must not be null.")
    ConnectorOutboundMessageAction action,
    @Valid
    @NotNull(message = "The from party must not be null.")
    ConnectorOutboundMessageParty fromParty,
    @Valid
    @NotNull(message = "The to party must not be null.")
    ConnectorOutboundMessageParty toParty
) {
}
