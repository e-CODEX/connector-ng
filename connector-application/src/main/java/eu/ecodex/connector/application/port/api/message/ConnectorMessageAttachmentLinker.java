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

/**
 * Use case for linking an uploaded attachment to a connector message.
 */
public interface ConnectorMessageAttachmentLinker {
    /**
     * Links the attachment identified by the given identifier to the message identified by the
     * given identifier, with the given attachment type.
     *
     * @param attachmentIdentifier identifier of the attachment to link
     * @param messageIdentifier    identifier of the message the attachment is linked to
     * @param attachmentType       the role of the attachment within the message
     *                             ({@link ConnectorAttachmentType})
     */
    void execute(
        String attachmentIdentifier,
        String messageIdentifier,
        ConnectorAttachmentType attachmentType);
}
