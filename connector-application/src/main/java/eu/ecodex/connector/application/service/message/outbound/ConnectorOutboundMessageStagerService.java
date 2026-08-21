/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.outbound;

import eu.ecodex.connector.application.exception.ConnectorMessageException;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundMessageStager;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorOutboundMessageStager} service.
 *
 * <p>This service stages an outbound {@link ConnectorBusinessMessage} by:
 * <ol>
 *     <li>Persisting the message entity</li>
 *     <li>Associating existing attachments with the persisted message</li>
 *     <li>Persisting the business content and linking its business document</li>
 * </ol>
 *
 * <p>The staging operation is executed within a transactional boundary to ensure
 * atomicity. If any step fails (e.g. a referenced attachment does not exist),
 * the entire operation is rolled back.
 *
 * <p>After successful staging, the message is expected to be forwarded to the
 * outbound processing pipeline.
 */
@Slf4j
@Service
@Transactional
public class ConnectorOutboundMessageStagerService implements ConnectorOutboundMessageStager {
    private final ConnectorMessageEventPublisher<ConnectorBusinessMessage>
        outboundMessagePipelinePublisher;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageAttachmentRepository attachmentRepository;

    /**
     * Creates a new {@code ConnectorOutboundMessageStagerService}.
     *
     * @param outboundMessagePipelinePublisher publisher used to publish
     *                                         {@link ConnectorBusinessMessage} entities to the
     *                                         outbound processing pipeline
     * @param messageRepository                repository used to persist
     *                                         {@link ConnectorBusinessMessage} entities
     * @param attachmentRepository             repository used to resolve and associate
     *                                         {@link ConnectorMessageAttachment} entities
     */
    public ConnectorOutboundMessageStagerService(
        @Qualifier("connectorJmsOutboundMessagePipelinePublisher")
        ConnectorMessageEventPublisher<ConnectorBusinessMessage> outboundMessagePipelinePublisher,
        ConnectorMessageRepository messageRepository,
        ConnectorMessageAttachmentRepository attachmentRepository) {
        this.outboundMessagePipelinePublisher = outboundMessagePipelinePublisher;
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public void execute(@NonNull ConnectorBusinessMessage message) {
        var identifier = message.identifier();
        log.info("Staging outbound message: [{}]", identifier);

        var createdMessage = this.messageRepository.save(message);
        attachAttachments(message.attachments(), identifier);
        persistBusinessDocument(message.businessContent(), identifier);
        this.outboundMessagePipelinePublisher.publish(createdMessage);
    }

    private void attachAttachments(
        List<ConnectorMessageAttachment> attachments, String messageIdentifier) {
        if (attachments != null) {
            attachments.forEach(
                attachment -> attachAttachment(
                    attachment, messageIdentifier, ConnectorAttachmentType.ATTACHMENT));
        }
    }

    private void attachAttachment(
        ConnectorMessageAttachment attachment, String messageIdentifier,
        ConnectorAttachmentType attachmentType) {
        var attachmentIdentifier = attachment.identifier();
        var existingAttachment = this.attachmentRepository.findByIdentifier(attachmentIdentifier);

        if (existingAttachment == null) {
            // TODO send back failed evidence message
            throw new IllegalStateException(
                "Attachment [%s] not found for the message [%s]".formatted(
                    attachmentIdentifier,
                    messageIdentifier
                )
            );
        }

        this.attachmentRepository.attachToMessage(attachmentIdentifier, messageIdentifier);
        this.attachmentRepository.updateType(attachmentIdentifier, attachmentType);
    }

    private void persistBusinessDocument(
        ConnectorMessageBusinessContent businessContent, String messageIdentifier) {
        if (businessContent == null) {
            throw new IllegalStateException("Business content is required");
        }

        var businessDocument = businessContent.businessDocument();

        if (businessDocument == null) {
            throw new ConnectorMessageException("Business document is required");
        }

        attachAttachment(
            businessContent.xmlContent(),
            messageIdentifier,
            ConnectorAttachmentType.BUSINESS_CONTENT
        );

        attachAttachment(
            businessDocument.attachment(),
            messageIdentifier,
            ConnectorAttachmentType.BUSINESS_DOCUMENT
        );
    }
}
