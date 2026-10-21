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
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

/**
 * Command describing an outbound test business message to be sent through the connector system.
 *
 * @param businessDomainIdentifier unique identifier of the business domain the message is
 *                                 associated with (must not be null)
 * @param backendMessageIdentifier unique identifier of the message in the backend system (must not
 *                                 be null)
 * @param as4PropertiesCommand     the AS4 properties of the test message (must not be null)
 * @param businessContent          the main business content of the message, including XML
 *                                 structures and associated documents (must not be null)
 * @param attachments              additional attachments associated with the message, such as
 *                                 supplementary documents or metadata; may be {@code null}, in
 *                                 which case it is replaced by an empty list, so the stored value
 *                                 is never {@code null}
 */
@Builder
public record ConnectorTestBusinessMessageCommand(
    @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
    @Nonnull String backendMessageIdentifier,
    @Nonnull ConnectorTestBusinessMessageAS4PropertiesCommand as4PropertiesCommand,
    @Nonnull ConnectorMessageBusinessContent businessContent,
    @Nullable List<ConnectorMessageAttachment> attachments
) {
    /**
     * Canonical constructor; replaces a {@code null} {@code attachments} list with an empty list.
     */
    public ConnectorTestBusinessMessageCommand {
        attachments = attachments == null ? List.of() : attachments;
    }
}
