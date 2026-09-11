/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;

/**
 * Component responsible for linking {@link ConnectorMessageAttachment} instances to specific
 * messages.
 *
 * <p>This class provides functionality for associating a given message attachment with a
 * specific message.</p>
 */
public interface ConnectorMessageAttachmentLinker {
    /**
     * Links a message attachment to a specific message.
     *
     * @param attachmentIdentifier The identifier of the message attachment.
     * @param messageIdentifier    The identifier of the message.
     * @param attachmentType       The type of the message attachment.
     */
    void execute(
        String attachmentIdentifier,
        String messageIdentifier,
        ConnectorAttachmentType attachmentType);
}
