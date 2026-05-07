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
import eu.ecodex.connector.domain.spi.ConnectorMessageAttachmentRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageEvidenceRepository;
import eu.ecodex.connector.domain.spi.ConnectorMessageRepository;
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
    private final ConnectorMessageAttachmentRepository attachmentRepository;
    private final ConnectorMessageEvidenceRepository evidenceRepository;
    private final ConnectorFileStorageProvider fileStorageProvider;
    private final ConnectorInboundMessagePipelineEventPublisher pipelineEventPublisher;

    /**
     * Creates a new listener instance.
     *
     * @param messageRepository repository used to update message delivery status
     */
    public ConnectorGatewayMessageListener(
            ConnectorMessageRepository messageRepository,
            ConnectorMessageAttachmentRepository attachmentRepository,
            ConnectorMessageEvidenceRepository evidenceRepository,
            ConnectorFileStorageProvider fileStorageProvider,
            ConnectorInboundMessagePipelineEventPublisher pipelineEventPublisher) {
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.evidenceRepository = evidenceRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.pipelineEventPublisher = pipelineEventPublisher;
    }

    /**
     * Handles incoming messages from the gateway reception queue. This method processes the
     * received JMS message, validates its headers, parses its content, and persists the information
     * as a domain-specific message representation. The processed message is then published as part
     * of a pipeline event.
     *
     * @param message the JMS MapMessage received from the gateway. It must not be null and must
     *                contain valid message headers and payloads to be correctly processed.
     *
     * @throws JMSException if an error occurs during processing, such as issues with accessing
     *                      message properties or payloads.
     */
    @Transactional
    @JmsListener(destination = "${connector.queues.gateway-reception-queue}")
    public void handle(@NonNull MapMessage message) throws JMSException {
        log.info("Receiving message from the gateway");

        validateMessageHeader(message);

        var messageIdentifier = message.getStringProperty("messageId");
        var as4Properties = parseAS4Properties(message);
        var payloads = parsePayloads(message);

        var incomingMessage = ConnectorMessage
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
                .evidences(payloads.evidences())
                .build();

        persistMessage(incomingMessage, payloads, messageIdentifier);
        pipelineEventPublisher.publish(incomingMessage);
    }

    private void validateMessageHeader(MapMessage message) throws JMSException {
        var messageType = message.getStringProperty("messageType");
        if (!"incomingMessage".equals(messageType)) {
            throw new IllegalArgumentException(
                    "Invalid Gateway reception messageType: " + messageType);
        }

        var messageId = message.getStringProperty("messageId");
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("Missing Gateway reception messageId");
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
        var fromParty = ConnectorParty.builder()
                                      .identifier(message.getStringProperty("fromPartyId"))
                                      .identifierType(message.getStringProperty("fromPartyType"))
                                      .role(message.getStringProperty("fromRole"))
                                      .roleType(ConnectorPartyRoleType.INITIATOR)
                                      .build();
        var toParty = ConnectorParty.builder()
                                    .identifier(message.getStringProperty("toPartyId"))
                                    .identifierType(message.getStringProperty("toPartyType"))
                                    .role(message.getStringProperty("toRole"))
                                    .roleType(ConnectorPartyRoleType.RESPONDER)
                                    .build();

        return ConnectorMessageAS4Properties.builder()
                                            .service(service)
                                            .action(action)
                                            .fromParty(fromParty)
                                            .toParty(toParty)
                                            .conversationIdentifier(message.getStringProperty(
                                                    "conversationId"))
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
                        description, CONTENT_TYPE_XML,
                        ConnectorAttachmentType.BUSINESS_CONTENT, payload
                );
                // Business document is bundled with ASICS payload
                businessContent = ConnectorMessageBusinessContent.builder()
                                                                 .xmlContent(content)
                                                                 .businessDocument(null)
                                                                 .build();
            } else if ("ASIC-S".equals(description)) {
                attachments.add(saveAndUploadAttachment(
                        description, CONTENT_TYPE_ASICS, ConnectorAttachmentType.ASICS, payload
                ));
            } else if ("tokenXML".equals(description)) {
                attachments.add(saveAndUploadAttachment(
                        description, CONTENT_TYPE_XML, ConnectorAttachmentType.XML_TOKEN, payload
                ));
            } else if (EVIDENCE_TYPE_NAMES.contains(description)) {
                var content = saveAndUploadAttachment(
                        description, CONTENT_TYPE_XML, ConnectorAttachmentType.EVIDENCE_XML, payload
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

        // TODO handle situation where the message is an evidence message, but no 'messageContent'
        if (businessContent == null) {
            throw new IllegalStateException(
                    "No 'messageContent' payload found in gateway message"
            );
        }

        return new ParsedPayloads(businessContent, attachments, evidences);
    }

    private void persistMessage(
            ConnectorMessage message,
            ParsedPayloads payloads,
            String messageIdentifier) {
        messageRepository.save(message);

        attachmentRepository.attachToMessage(
                payloads.businessContent().xmlContent().identifier(),
                messageIdentifier
        );

        payloads.evidences().forEach(evidence -> {
            evidenceRepository.save(evidence, messageIdentifier);
            attachmentRepository.attachToMessage(
                    evidence.attachment().identifier(), messageIdentifier
            );
        });

        payloads.attachments().forEach(attachment ->
                                               attachmentRepository.attachToMessage(
                                                       attachment.identifier(), messageIdentifier
                                               ));
    }

    private ConnectorMessageAttachment saveAndUploadAttachment(
            String name,
            String contentType,
            ConnectorAttachmentType type,
            byte[] payload) {
        var identifier = UUID.randomUUID() + "_" + name;
        var attachment = ConnectorMessageAttachment.builder()
                                                   .identifier(identifier)
                                                   .name(name)
                                                   .contentType(contentType)
                                                   .size(payload.length)
                                                   .description("File from gateway")
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
