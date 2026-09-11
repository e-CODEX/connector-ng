/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.message.inbound;

import eu.ecodex.connector.application.port.api.message.ConnectorMessageAttachmentLinker;
import eu.ecodex.connector.application.port.api.message.inbound.ConnectorInboundBusinessMessageStager;
import eu.ecodex.connector.application.port.spi.ConnectorMessageEventPublisher;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ConnectorInboundBusinessMessageStager} interface.
 *
 * <p>The staging operation is executed within a transactional boundary to ensure
 * atomicity. If any step fails (e.g., a referenced attachment does not exist), the entire operation
 * is rolled back.
 *
 * <p>After successful staging, the message is expected to be forwarded to the
 * inbound processing pipeline.
 */
@Slf4j
@Service
@Transactional
public class ConnectorInboundBusinessMessageStagerService implements
    ConnectorInboundBusinessMessageStager {
    private final ConnectorMessageEventPublisher<ConnectorBusinessMessage>
        inboundMessagePipelinePublisher;
    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorMessageAttachmentLinker attachmentLinkerService;

    /**
     * Constructs a new instance of the {@link ConnectorInboundBusinessMessageStagerService} with
     * the specified dependencies.
     *
     * @param inboundMessagePipelinePublisher The publisher for inbound business message events.
     * @param messageRepository               The repository for managing business messages.
     * @param evidenceRepository              The repository for managing a message evidences.
     * @param attachmentLinkerService         The linker service for managing message attachments.
     */
    public ConnectorInboundBusinessMessageStagerService(
        @Qualifier("connectorJmsInboundMessagePipelinePublisher")
        ConnectorMessageEventPublisher<ConnectorBusinessMessage> inboundMessagePipelinePublisher,
        ConnectorMessageRepository messageRepository,
        ConnectorMessageEvidenceRepository evidenceRepository,
        ConnectorMessageAttachmentLinker attachmentLinkerService
    ) {
        this.inboundMessagePipelinePublisher = inboundMessagePipelinePublisher;
        this.messageRepository = messageRepository;
        this.evidenceRepository = evidenceRepository;
        this.attachmentLinkerService = attachmentLinkerService;
    }

    @Override
    public void execute(@NonNull ConnectorBusinessMessage message) {
        var identifier = message.identifier();
        log.info("Staging inbound business message {}", identifier);
        var transportedEvidences = message.transportedEvidences();

        if (transportedEvidences == null) {
            throw new IllegalStateException("Transported evidences must not be null");
        }

        if (transportedEvidences.isEmpty()) {
            throw new IllegalStateException("Transported evidences must not be empty");
        }

        var createdMessage = this.messageRepository.save(message);
        createdMessage = createdMessage.toBuilder()
                                       .transportedEvidences(transportedEvidences)
                                       .build();
        attachAttachments(message.attachments(), identifier);
        persistBusinessDocument(message.businessContent(), identifier);
        persistEvidences(transportedEvidences, identifier);
        inboundMessagePipelinePublisher.publish(createdMessage);
    }

    private void attachAttachments(
        List<ConnectorMessageAttachment> attachments, String messageIdentifier) {
        if (attachments != null) {
            attachments.forEach(
                attachment -> attachmentLinkerService.execute(
                    attachment.identifier(), messageIdentifier, attachment.type()));
        }
    }

    private void persistBusinessDocument(
        ConnectorMessageBusinessContent businessContent, String messageIdentifier) {
        if (businessContent == null) {
            throw new IllegalStateException("Business content is required");
        }

        attachmentLinkerService.execute(
            businessContent.xmlContent().identifier(),
            messageIdentifier,
            ConnectorAttachmentType.BUSINESS_CONTENT
        );

        // At this step, the business document is still embedded in the asics container
        // will be extracted and persisted in the security validation step
    }

    private void persistEvidences(
        List<ConnectorMessageEvidence> evidences,
        String messageIdentifier) {
        evidences.forEach(evidence -> {
            if (evidence.content() == null) {
                throw new IllegalStateException(
                    "Evidence content is null for evidence %s".formatted(evidence.type())
                );
            }
            evidenceRepository.save(evidence, messageIdentifier);
        });
    }
}
