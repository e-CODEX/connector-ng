/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.impl.message.outbound;

import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageStager;
import eu.ecodex.connector.domain.api.ConnectorEventPublisher;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorOutboundMessageStager} service.
 *
 * <p>This service stages an outbound {@link ConnectorMessage} by:
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
@Service
public class ConnectorOutboundMessageStagerService implements ConnectorOutboundMessageStager {
    private final ConnectorEventPublisher pipelineEventPublisher;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorMessageBusinessContentRepository businessContentRepository;

    /**
     * Creates a new {@code ConnectorOutboundMessageStagerService}.
     *
     * @param messageRepository         repository used to persist {@link ConnectorMessage}
     *                                  entities
     * @param attachmentRepository      repository used to resolve and associate
     *                                  {@link ConnectorMessageAttachment} entities
     * @param businessContentRepository repository used to persist
     *                                  {@link ConnectorMessageBusinessContent} entities linked to a
     *                                  message
     */
    public ConnectorOutboundMessageStagerService(
            @Qualifier("connectorOutboundMessagePipelineEventPublisher")
            ConnectorEventPublisher pipelineEventPublisher,
            ConnectorMessageRepository messageRepository,
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorMessageBusinessContentRepository businessContentRepository) {
        this.pipelineEventPublisher = pipelineEventPublisher;
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.businessContentRepository = businessContentRepository;
    }

    @Override
    @Transactional
    public void stage(ConnectorMessage message) {
        var createdMessage = this.messageRepository.save(message);
        var messageIdentifier = createdMessage.identifier();
        attachAttachments(message.attachments(), messageIdentifier);
        persistBusinessDocument(message.businessContent(), messageIdentifier);
        this.pipelineEventPublisher.publish(createdMessage);
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
            // TODO send back failed evidence
            throw new IllegalStateException(
                    String.format(
                            "Attachment [%s] not found for the message [%s]",
                            attachmentIdentifier, messageIdentifier
                    )
            );
        }

        this.attachmentRepository.attachToMessage(attachmentIdentifier, messageIdentifier);
        this.attachmentRepository.updateType(attachmentIdentifier, attachmentType);
    }

    private void persistBusinessDocument(
            ConnectorMessageBusinessContent businessContent, String messageIdentifier) {
        attachAttachment(
                businessContent.xmlContent(),
                messageIdentifier,
                ConnectorAttachmentType.BUSINESS_CONTENT
        );

        attachAttachment(
                businessContent.businessDocument().attachment(),
                messageIdentifier,
                ConnectorAttachmentType.BUSINESS_DOCUMENT
        );

        this.businessContentRepository.save(businessContent, messageIdentifier);
    }
}
