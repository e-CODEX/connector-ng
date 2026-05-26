/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.messaging.listener;

import eu.ecodex.connector.application.service.impl.message.ConnectorMessageIdGenerator;
import eu.ecodex.connector.domain.ConnectorDefaults;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentStorage;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.domain.spi.ConnectorFileStorageProvider;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageBusinessContentRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.message.ConnectorMessageRepository;
import eu.ecodex.connector.infrastructure.messaging.publisher.ConnectorInboundMessagePipelineEventPublisher;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * JMS listener responsible for handling message submitted by the gateway to the connector.
 */
@Slf4j
@Component
public class ConnectorGatewayMessageListener {
    private static final String CONTENT_TYPE_XML = "text/xml";
    private static final String CONTENT_TYPE_ASICS = "application/vnd.etsi.asic-s+zip";

    private static final Set<String> EVIDENCE_TYPE_NAMES =
            Arrays.stream(ConnectorEvidenceType.values())
                    .map(Enum::name)
                    .collect(Collectors.toUnmodifiableSet());

    private final ConnectorMessageRepository messageRepository;
    private final ConnectorMessageBusinessContentRepository businessContentRepository;
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final ConnectorInboundMessagePipelineEventPublisher pipelineEventPublisher;
    private final ConnectorMessageIdGenerator messageIdGenerator;

    /**
     * Creates a new listener instance.
     *
     * @param messageRepository repository used to update message delivery status
     */
    public ConnectorGatewayMessageListener(
            ConnectorMessageRepository messageRepository,
            ConnectorMessageBusinessContentRepository businessContentRepository,
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorMessageEvidenceRepository evidenceRepository,
            ConnectorFileStorageProvider fileStorageProvider,
            ConnectorInboundMessagePipelineEventPublisher pipelineEventPublisher,
            ConnectorMessageIdGenerator messageIdGenerator) {
        this.messageRepository = messageRepository;
        this.businessContentRepository = businessContentRepository;
        this.attachmentRepository = attachmentRepository;
        this.evidenceRepository = evidenceRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.pipelineEventPublisher = pipelineEventPublisher;
        this.messageIdGenerator = messageIdGenerator;
    }

    /**
     * Handles incoming messages from the gateway reception queue. This method processes the
     * received JMS message, validates its headers, parses its content, and persists the information
     * as a domain-specific message representation. The processed message is then published as part
     * of a pipeline event.
     *
     * @param message the JMS MapMessage received from the gateway. It must not be null and must
     *                contain valid message headers and payloads to be correctly processed.
     * @throws JMSException if an error occurs during processing, such as issues with accessing
     *                      message properties or payloads.
     */
    @Transactional
    @JmsListener(destination = "${connector.queues.gateway-reception-queue}")
    public void handle(@NonNull MapMessage message) throws JMSException {
        log.info("Receiving message from the gateway");

        validateMessageHeader(message);

        var as4Properties = parseAS4Properties(message);
        var payloads = parsePayloads(message);

        if (payloads.businessContent == null && !payloads.evidences.isEmpty()) {
            log.info("Received message from the gateway is a confirmation message");
        } else if (payloads.businessContent != null) {
            log.info("Received message from the gateway is a business message");
        } else {
            log.info(
                    "Received message from the gateway is neither evidence nor a business message"
            );
            throw new IllegalStateException(
                    "Received message from the gateway is neither evidence nor a business message"
            );
        }

        var messageIdentifier = messageIdGenerator.generateIdentifier();

        var inboundMessage = ConnectorMessage
                .builder()
                .identifier(messageIdentifier)
                .businessDomainIdentifier(ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID)
                .as4Properties(as4Properties)
                .direction(
                        ConnectorMessageDirection.GATEWAY_TO_BACKEND
                )
                .gatewayName(ConnectorDefaults.DEFAULT_GATEWAY_NAME)
                .businessContent(payloads.businessContent())
                .attachments(payloads.attachments())
                .evidences(null)
                .transportedEvidences(payloads.evidences())
                .build();

        pipelineEventPublisher.publish(persistMessage(inboundMessage, payloads, messageIdentifier));
    }

    private void validateMessageHeader(MapMessage message) throws JMSException {
        var messageType = message.getStringProperty("messageType");
        if (!"incomingMessage".equals(messageType)) {
            throw new IllegalArgumentException(
                    "Invalid Gateway reception messageType: " + messageType);
        }

        var ebmsMessageIdentifier = message.getStringProperty("messageId");

        if (!StringUtils.hasText(ebmsMessageIdentifier)) {
            throw new IllegalArgumentException(
                    "Invalid Gateway reception messageId: " + ebmsMessageIdentifier);
        }

        int total = message.getIntProperty("totalNumberOfPayloads");

        if (total <= 0) {
            throw new IllegalArgumentException(
                    "Invalid Gateway reception totalNumberOfPayloads: " + total);
        }
    }

    private ConnectorMessageAS4Properties parseAS4Properties(MapMessage message)
            throws JMSException {
        var service = ConnectorService.builder()
                .name(message.getStringProperty("service"))
                .type(message.getStringProperty("serviceType"))
                .build();
        var action = ConnectorAction.builder()
                .name(message.getStringProperty("action"))
                .build();
        var fromPartyId = message.getStringProperty("fromPartyId");
        var fromPartyType = message.getStringProperty("fromPartyType");
        var fromRole = message.getStringProperty("fromRole");

        if (!StringUtils.hasText(fromPartyId) || !StringUtils.hasText(fromPartyType)
                || !StringUtils.hasText(fromRole)) {
            throw new IllegalArgumentException(" fromParty is not allowed to be null");
        }

        var fromParty = ConnectorParty.builder()
                .identifier(fromPartyId)
                .identifierType(fromPartyType)
                .role(fromRole)
                .roleType(ConnectorPartyRoleType.INITIATOR)
                .build();
        var toPartyId = message.getStringProperty("toPartyId");
        var toPartyType = message.getStringProperty("toPartyType");
        var toRole = message.getStringProperty("toRole");

        if (!StringUtils.hasText(toPartyId) || !StringUtils.hasText(toPartyType)
                || !StringUtils.hasText(toRole)) {
            throw new IllegalArgumentException(" toParty is not allowed to be null");
        }

        var toParty = ConnectorParty.builder()
                .identifier(toPartyId)
                .identifierType(toPartyType)
                .role(toRole)
                .roleType(ConnectorPartyRoleType.RESPONDER)
                .build();

        return ConnectorMessageAS4Properties.builder()
                .service(service)
                .action(action)
                .fromParty(fromParty)
                .toParty(toParty)
                .conversationIdentifier(message.getStringProperty(
                        "conversationId"))
                .ebmsMessageIdentifier(message.getStringProperty("messageId"))
                .referenceToIdentifier(message.getStringProperty(
                        "refToMessageId"))
                .originalSender(message.getStringProperty(
                        "originalSender"))
                .finalRecipient(message.getStringProperty(
                        "finalRecipient"))
                .build();
    }

    private ParsedPayloads parsePayloads(MapMessage message) throws JMSException {
        int total = message.getIntProperty("totalNumberOfPayloads");
        var attachments = new ArrayList<ConnectorMessageAttachment>();
        var evidences = new ArrayList<ConnectorMessageEvidence>();
        ConnectorMessageBusinessContent businessContent = null;

        for (int i = 1; i <= total; i++) {
            var prefix = "payload_" + i;
            var description = message.getStringProperty(prefix + "_description");
            var payload = message.getBytes(prefix);

            if (!StringUtils.hasText(description)) {
                throw new IllegalArgumentException(
                        "Missing description for payload at index " + i);
            }

            if ("messageContent".equals(description)) {
                var content = saveAndUploadAttachment(
                        description,
                        CONTENT_TYPE_XML,
                        "Inbound message business content",
                        ConnectorAttachmentType.BUSINESS_CONTENT,
                        payload
                );
                // Business document is bundled with ASICS payload
                businessContent = ConnectorMessageBusinessContent.builder()
                        .xmlContent(content)
                        .businessDocument(null)
                        .build();
            } else if ("ASIC-S".equals(description)) {
                attachments.add(saveAndUploadAttachment(
                        description,
                        CONTENT_TYPE_ASICS,
                        "Inbound message ASIC-S component",
                        ConnectorAttachmentType.ASICS,
                        payload
                ));
            } else if ("tokenXML".equals(description)) {
                attachments.add(saveAndUploadAttachment(
                        description,
                        CONTENT_TYPE_XML,
                        "Inbound message XML Trust OK Token",
                        ConnectorAttachmentType.XML_TOKEN,
                        payload
                ));
            } else if (EVIDENCE_TYPE_NAMES.contains(description)) {
                var content = saveAndUploadAttachment(
                        description,
                        CONTENT_TYPE_XML,
                        "Inbound message evidence",
                        ConnectorAttachmentType.EVIDENCE_XML,
                        payload
                );
                evidences.add(
                        ConnectorMessageEvidence.builder()
                                .type(ConnectorEvidenceType.valueOf(description))
                                .attachment(content)
                                .build());
            } else {
                log.warn(
                        "Unrecognised payload description '{}' at index {} — skipping",
                        description, i
                );
            }
        }

        return new ParsedPayloads(businessContent, attachments, evidences);
    }

    private ConnectorMessage persistMessage(
            ConnectorMessage message,
            ParsedPayloads payloads,
            String messageIdentifier) {
        messageRepository.save(message);

        if (payloads.businessContent != null) {
            businessContentRepository.save(payloads.businessContent, messageIdentifier);
            attachmentRepository.attachToMessage(
                    payloads.businessContent().xmlContent().identifier(),
                    messageIdentifier
            );
        }

        payloads.evidences().forEach(evidence -> {
            evidenceRepository.save(evidence, messageIdentifier);

            if (evidence.attachment() == null) {
                throw new IllegalStateException(
                        "Evidence attachment is null for evidence " + evidence.type()
                );
            }

            attachmentRepository.attachToMessage(
                    evidence.attachment().identifier(), messageIdentifier
            );
        });

        payloads.attachments().forEach(attachment ->
                                               attachmentRepository.attachToMessage(
                                                       attachment.identifier(), messageIdentifier
                                               ));

        return messageRepository.findByIdentifier(messageIdentifier);
    }

    private ConnectorMessageAttachment saveAndUploadAttachment(
            String name,
            String contentType,
            String description,
            ConnectorAttachmentType type,
            byte[] payload) {
        var identifier = UUID.randomUUID() + "_" + name;
        var attachment = ConnectorMessageAttachment.builder()
                .identifier(identifier)
                .name(name)
                .contentType(contentType)
                .size(payload.length)
                .description(description)
                .storage(ConnectorAttachmentStorage.S3_BUCKET)
                .type(type)
                .build();

        fileStorageProvider.save(attachment, payload);
        return attachmentRepository.save(attachment);
    }

    private record ParsedPayloads(
            ConnectorMessageBusinessContent businessContent,
            List<ConnectorMessageAttachment> attachments,
            List<ConnectorMessageEvidence> evidences) {
    }
}
