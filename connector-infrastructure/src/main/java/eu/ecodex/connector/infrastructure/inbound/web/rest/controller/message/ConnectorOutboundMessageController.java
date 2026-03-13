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
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageAS4Properties;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageBusinessDocument;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageDetachedSignature;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the REST controller for managing an outbound message within the connector system.
 */
@RestController
public class ConnectorOutboundMessageController implements ConnectorOutboundMessageApi {
    private final ConnectorOutboundMessageReceiver messageStagingService;
    private final ConnectorBackendClientVerifier backendClientVerifierService;

    public ConnectorOutboundMessageController(
            ConnectorOutboundMessageReceiver messageStagingService,
            ConnectorBackendClientVerifier backendClientVerifierService) {
        this.messageStagingService = messageStagingService;
        this.backendClientVerifierService = backendClientVerifierService;
    }

    @Override
    public ConnectorOutboundMessageDto submit(
            MultipartFile businessXMLDocument,
            ConnectorOutboundMessageRequest messageMetadata) throws IOException {
        var message = toDomain(messageMetadata, businessXMLDocument.getBytes());

        var processedMessage = messageStagingService.register(message);

        return toDto(processedMessage);
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

    private ConnectorMessage toDomain(
            ConnectorOutboundMessageRequest request, byte[] xmlBusinessDocument) {
        // TODO current cn is fake, retrieve the certificate dn from user principal
        var backendClientName = this.backendClientVerifierService.getBackendClient("cn=alice");
        return ConnectorMessage
                .builder()
                .businessDomainIdentifier(
                        setBusinessDomainIdentifier(request.businessDomainIdentifier())
                )
                .backendMessageIdentifier(request.backendMessageIdentifier())
                .referenceToBackendMessageIdentifier(request.referenceToBackendMessageIdentifier())
                .backendName(backendClientName)
                .direction(ConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .as4Properties(
                        toDomainAS4Properties(request.as4Properties())
                )
                .businessContent(toBusinessContent(request.businessContent(), xmlBusinessDocument))
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
                .referenceToIdentifier(as4Properties.referenceToIdentifier())
                .conversationIdentifier(as4Properties.conversationIdentifier())
                .service(service)
                .action(action)
                .fromParty(fromParty)
                .toParty(toParty)
                .build();
    }

    private ConnectorBusinessDomainIdentifier setBusinessDomainIdentifier(String identifier) {
        if (identifier == null) {
            return ConnectorBusinessDomain.DEFAULT_BUSINESS_DOMAIN_ID;
        }

        return ConnectorBusinessDomainIdentifier
                .builder()
                .messageLaneIdentifier(identifier)
                .build();
    }

    private ConnectorMessageBusinessContent toBusinessContent(
            ConnectorOutboundMessageBusinessDocument business, byte[] xmlBusinessDocument) {
        var businessDocument = ConnectorMessageBusinessDocument
                .builder()
                .attachment(toAttachment(business.attachmentIdentifier()))
                .detachedSignature(toDetachedSignature(business.detachedSignature()))
                .aesType(business.aesType())
                .build();

        return ConnectorMessageBusinessContent
                .builder()
                .xmlContent(new String(xmlBusinessDocument, StandardCharsets.UTF_8))
                .businessDocument(businessDocument)
                .build();
    }

    private DetachedSignature toDetachedSignature(
            ConnectorOutboundMessageDetachedSignature detachedSignature) {
        if (detachedSignature == null) {
            return null;
        }

        return DetachedSignature
                .builder()
                .name(detachedSignature.name())
                .signature(detachedSignature.signature())
                .mimeType(detachedSignature.mimeType())
                .build();
    }

    private ConnectorMessageAttachment toAttachment(String identifier) {
        return ConnectorMessageAttachment
                .builder()
                .identifier(identifier)
                .build();
    }

    private List<ConnectorMessageAttachment> toAttachments(List<String> identifiers) {
        if (identifiers == null) {
            return null;
        }

        return identifiers.stream()
                          .map(this::toAttachment)
                          .toList();
    }
}
