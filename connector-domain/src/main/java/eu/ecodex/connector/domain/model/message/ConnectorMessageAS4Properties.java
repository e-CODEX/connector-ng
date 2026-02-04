/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;


/**
 * Represents the AS4-specific properties of a connector message.
 *
 * <p>This record encapsulates metadata relevant to AS4 message exchange,
 * including message identifiers and information about the sender, recipient, and involved parties.
 *
 * <p>Instances of this record are used as part of the
 * {@link ConnectorMessage} to store the AS4-specific properties for message tracking, routing, and
 * processing purposes.
 *
 * @param ebmsMessageIdentifier  The unique uuid of the ebMS message. Maybe null.
 * @param referenceToIdentifier  The uuid of a referenced message, if applicable. Maybe
 *                               null.
 * @param conversationIdentifier The uuid of the conversation to which this message belongs.
 *                               Maybe null.
 * @param originalSender         The original sender uuid for the message. Must not be blank.
 * @param finalRecipient         The final recipient uuid for the message. Must not be blank.
 * @param service                The service associated with this message. Must not be null.
 * @param action                 The action performed in the context of this message. Must not be
 *                               null.
 * @param fromParty              The party originating the message. Maybe null.
 * @param toParty                The party receiving the message. Maybe null.
 */
@Builder(toBuilder = true)
public record ConnectorMessageAS4Properties(
        @Nullable String ebmsMessageIdentifier,
        @Nullable String referenceToIdentifier,
        @Nullable String conversationIdentifier,
        @NotBlank String originalSender,
        @NotBlank String finalRecipient,
        @Nonnull ConnectorService service,
        @Nonnull ConnectorAction action,
        ConnectorParty fromParty,
        ConnectorParty toParty
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{ebmsMessageIdentifier=%s, referenceToIdentifier=%s, conversationIdentifier=%s, "
                + "originalSender=%s, finalRecipient=%s, service=%s, action=%s, fromParty=%s, "
                + "toParty=%s}",
                ebmsMessageIdentifier, referenceToIdentifier, conversationIdentifier,
                originalSender, finalRecipient, service, action, fromParty, toParty
        );
    }
}
