/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.message;

import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import eu.ecodex.connector.domain.model.message.content.DetachedSignature;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorAttachmentUploadException;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageAS4Properties;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageBusinessContent;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageDetachedSignature;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the REST controller for managing messages within the connector system.
 */
@RestController
public class ConnectorMessageController implements ConnectorMessageApi {
    private final ConnectorOutboundMessageReceiver outboundMessageReceiver;
    private final ConnectorBackendClientVerifier backendClientVerifierService;
    private final ConnectorUploadAttachments uploadAttachmentsService;

    /**
     * Constructs a new instance of ConnectorMessageController.
     *
     * @param outboundMessageReceiver      The service responsible for receiving and managing
     *                                     outbound messages.
     * @param backendClientVerifierService The service used for verifying backend clients.
     * @param uploadAttachmentsService     The service for handling file attachments during message
     *                                     processing.
     */
    public ConnectorMessageController(
            ConnectorOutboundMessageReceiver outboundMessageReceiver,
            ConnectorBackendClientVerifier backendClientVerifierService,
            ConnectorUploadAttachments uploadAttachmentsService) {
        this.outboundMessageReceiver = outboundMessageReceiver;
        this.backendClientVerifierService = backendClientVerifierService;
        this.uploadAttachmentsService = uploadAttachmentsService;
    }

    @Override
    public ConnectorOutboundMessageDto submitOutboundMessage(
            ConnectorOutboundMessageRequest request) throws IOException {
        var message = toDomain(request);
        var registeredMessage = outboundMessageReceiver.register(message);

        return toDto(registeredMessage);
    }

    private ConnectorOutboundMessageDto toDto(ConnectorMessage message) {
        return ConnectorOutboundMessageDto
                .builder()
                .identifier(message.identifier())
                .backendMessageIdentifier(message.backendMessageIdentifier())
                .referenceToBackendMessageIdentifier(message.referenceToBackendMessageIdentifier())
                .direction(Objects.requireNonNull(message.direction()))
                .build();
    }

    private ConnectorMessage toDomain(ConnectorOutboundMessageRequest request) throws IOException {
        // TODO current cn is fake, retrieve the certificate dn from user principal
        var backendClientName = this.backendClientVerifierService.getBackendClient("cn=alice");
        return ConnectorMessage
                .builder()
                .businessDomainIdentifier(
                        resolveBusinessDomainIdentifier(request.businessDomainIdentifier())
                )
                .backendMessageIdentifier(request.backendMessageIdentifier())
                .referenceToBackendMessageIdentifier(null)
                .backendName(backendClientName)
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .as4Properties(
                        toDomainAS4Properties(request.as4Properties())
                )
                .businessContent(toBusinessContent(request.businessContent()))
                .attachments(toAttachments(request.attachments()))
                .build();
    }

    private ConnectorMessageAS4Properties toDomainAS4Properties(
            ConnectorOutboundMessageAS4Properties as4Properties) {
        var action = ConnectorAction
                .builder()
                .name(as4Properties.action().name())
                .build();
        var service = ConnectorService
                .builder()
                .name(as4Properties.service().name())
                .type(as4Properties.service().type())
                .build();
        var fromParty = ConnectorParty
                .builder()
                .identifier(as4Properties.fromParty().identifier())
                .identifierType(as4Properties.fromParty().identifierType())
                .role(as4Properties.fromParty().role())
                .roleType(ConnectorPartyRoleType.INITIATOR)
                .build();
        var toParty = ConnectorParty
                .builder()
                .identifier(as4Properties.toParty().identifier())
                .identifierType(as4Properties.toParty().identifierType())
                .role(as4Properties.toParty().role())
                .roleType(ConnectorPartyRoleType.RESPONDER)
                .build();

        return ConnectorMessageAS4Properties
                .builder()
                .originalSender(as4Properties.originalSender())
                .finalRecipient(as4Properties.finalRecipient())
                .ebmsMessageIdentifier(as4Properties.ebmsIdentifier())
                .conversationIdentifier(as4Properties.conversationIdentifier())
                .service(service)
                .action(action)
                .fromParty(fromParty)
                .toParty(toParty)
                .build();
    }

    private ConnectorBusinessDomainIdentifier resolveBusinessDomainIdentifier(String identifier) {
        if (identifier == null) {
            return ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID;
        }

        return ConnectorBusinessDomainIdentifier
                .builder()
                .messageLaneIdentifier(identifier)
                .build();
    }

    private ConnectorMessageBusinessContent toBusinessContent(
            ConnectorOutboundMessageBusinessContent businessContent) throws IOException {
        var businessDocumentRequest = businessContent.businessDocument();
        var businessDocument = ConnectorMessageBusinessDocument
                .builder()
                .attachment(toAttachment(businessContent.businessDocument().document()))
                .detachedSignature(toDetachedSignature(businessDocumentRequest.detachedSignature()))
                .aesType(businessDocumentRequest.aesType())
                .build();

        return ConnectorMessageBusinessContent
                .builder()
                .xmlContent(toAttachment(businessContent.contentFile()))
                .businessDocument(businessDocument)
                .build();
    }

    private DetachedSignature toDetachedSignature(
            ConnectorOutboundMessageDetachedSignature detachedSignature) throws IOException {
        if (detachedSignature == null || detachedSignature.signature() == null) {
            return null;
        }

        return DetachedSignature
                .builder()
                .name(detachedSignature.signature().getName())
                .signature(detachedSignature.signature().getBytes())
                .mimeType(detachedSignature.mimeType())
                .build();
    }

    private ConnectorMessageAttachment toAttachment(String identifier) {
        return ConnectorMessageAttachment
                .builder()
                .identifier(identifier)
                .build();
    }

    private ConnectorMessageAttachment toAttachment(MultipartFile file) throws IOException {
        var tempLocation = Files.createTempFile("upload_", file.getName());

        try {
            file.transferTo(tempLocation);
            var uploadCommand = FileUploadCommand.builder()
                                                 .contentType(file.getContentType())
                                                 .filename(file.getName())
                                                 .tempFileLocation(tempLocation)
                                                 .size(file.getSize())
                                                 .build();
            return uploadAttachmentsService.execute(List.of(uploadCommand)).getFirst();
        } catch (Exception e) {
            throw new ConnectorAttachmentUploadException(
                    "Failed to upload attachment: " + file.getName(), e);
        } finally {
            // Always runs — covers both success and failure paths
            Files.deleteIfExists(tempLocation);
        }
    }

    private List<ConnectorMessageAttachment> toAttachments(List<String> identifiers) {
        if (identifiers == null) {
            return new ArrayList<>();
        }

        return identifiers.stream()
                          .map(this::toAttachment)
                          .toList();
    }
}
