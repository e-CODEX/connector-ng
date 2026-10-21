/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.c2ctest;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingModeParty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

/**
 * Represents a record for sending outbound business messages through the connector system.
 *
 * @param businessDomainIdentifier Represents the unique identifier of the business domain
 *                                 associated with this message.
 * @param ebmsIdentifier           Represents Ebms message identifier
 * @param conversationIdentifier   Represents the unique identifier of the conversation to which
 *                                 this message belongs.
 * @param originalSender           Represents the original sender of the business message; must not
 *                                 be {@code null}.
 * @param finalRecipient           Represents the final recipient of the business message; must not
 *                                 be {@code null}.
 * @param fromParty                The {@link ConnectorProcessingModeParty} representing the sending
 *                                 party in the AS4 exchange; must not be {@code null}
 * @param toParty                  The {@link ConnectorProcessingModeParty} representing the
 *                                 receiving party in the AS4 exchange; must not be {@code null}
 */
@Builder
public record ConnectorTestBusinessMessageAS4PropertiesCommand(
    @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nullable String ebmsIdentifier,
    @Nonnull String conversationIdentifier,
    @Nonnull String originalSender,
    @Nonnull String finalRecipient,
    @Nonnull ConnectorProcessingModeParty fromParty,
    @Nonnull ConnectorProcessingModeParty toParty
) {
}
