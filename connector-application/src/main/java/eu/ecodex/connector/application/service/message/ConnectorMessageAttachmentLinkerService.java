/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message;

import eu.ecodex.connector.application.port.api.message.ConnectorMessageAttachmentLinker;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import org.springframework.stereotype.Component;

/**
 * Component responsible for linking {@link ConnectorMessageAttachment} instances to specific
 * messages.
 *
 * <p>This class provides functionality for associating a given message attachment with a
 * specific message, along with updating the attachment's type. It ensures that only existing
 * attachments can be linked to messages and validates the presence of the attachment before
 * attempting the association.
 */
@Component
public class ConnectorMessageAttachmentLinkerService implements ConnectorMessageAttachmentLinker {
    private final ConnectorMessageAttachmentRepository attachmentRepository;

    public ConnectorMessageAttachmentLinkerService(
        ConnectorMessageAttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public void execute(
        String attachmentIdentifier,
        String messageIdentifier,
        ConnectorAttachmentType attachmentType) {
        var existingAttachment = attachmentRepository.findByIdentifier(attachmentIdentifier);

        if (existingAttachment == null) {
            throw new IllegalStateException(
                "Attachment [%s] not found for the message [%s]".formatted(
                    attachmentIdentifier, messageIdentifier));
        }

        attachmentRepository.attachToMessage(attachmentIdentifier, messageIdentifier);
        attachmentRepository.updateType(attachmentIdentifier, attachmentType);
    }
}
