/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.soap.controller;

import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.service.usecase.message.outbound.ConnectorOutboundMessageReceiver;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessagesType;
import eu.ecodex.connector.domain.transition.EmptyRequestType;
import eu.ecodex.connector.domain.transition.GetMessageByIdRequest;
import eu.ecodex.connector.domain.transition.ListPendingMessageIdsResponse;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorInternalServerException;
import eu.ecodex.connector.infrastructure.inbound.web.soap.helper.AttachmentHelpers;
import eu.ecodex.connector.infrastructure.inbound.web.soap.helper.MessageHelpers;
import jakarta.xml.ws.soap.MTOM;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Spring-managed web service controller responsible for handling backend message submissions for
 * the connector.
 *
 * <p>This component implements {@link DomibusConnectorBackendWebService}
 * and acts as the integration entry point between backend systems and the connector domain.
 */
@Slf4j
@MTOM
@Component
public class ConnectorBackendWebServiceController implements DomibusConnectorBackendWebService {
    private final ConnectorOutboundMessageReceiver messageStagingService;
    private final ConnectorUploadAttachments uploadAttachmentsService;
    private final ConnectorBackendClientVerifier backendClientVerifierService;

    /**
     * Creates a new backend web service controller.
     *
     * @param messageStagingService    the service responsible for staging outbound and inbound
     *                                 messages (must not be null)
     * @param uploadAttachmentsService the service responsible for handling attachment uploads (must
     *                                 not be null)
     */
    public ConnectorBackendWebServiceController(
            ConnectorOutboundMessageReceiver messageStagingService,
            ConnectorUploadAttachments uploadAttachmentsService,
            ConnectorBackendClientVerifier backendClientVerifierService) {
        this.messageStagingService = messageStagingService;
        this.uploadAttachmentsService = uploadAttachmentsService;
        this.backendClientVerifierService = backendClientVerifierService;
    }

    @Override
    public EmptyRequestType acknowledgeMessage(
            DomibusConnectorMessageResponseType acknowledgeMessageRequest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public ListPendingMessageIdsResponse listPendingMessageIds(
            EmptyRequestType listPendingMessageIdsRequest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public DomibusConnectorMessagesType requestMessages(EmptyRequestType requestMessagesRequest) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(
            DomibusConnectorMessageType submitMessageRequest) {
        var answer = new DomibsConnectorAcknowledgementType();

        try {
            var attachmentIdentifiers = persistAttachments(
                    submitMessageRequest.getMessageAttachments()
            );
            var businessContent = submitMessageRequest.getMessageContent();
            var businessDocumentAttachmentIdentifier = persistBusinessDocument(
                    businessContent.getDocument()
            );
            var businessContentAttachmentIdentifier = persistBusinessContent(businessContent);
            // TODO current cn is fake, retrieve the certificate dn from user principal
            var backendClientName = this.backendClientVerifierService.getBackendClient("cn=alice");
            var parsedMessage = MessageHelpers.toDomain(
                    submitMessageRequest,
                    attachmentIdentifiers,
                    businessContentAttachmentIdentifier,
                    businessDocumentAttachmentIdentifier,
                    backendClientName
            );


            var createdMessage = this.messageStagingService.register(parsedMessage);

            answer.setMessageId(createdMessage.identifier());
            answer.setResult(true);
        } catch (Exception e) {
            log.error("error submitting message to the connector via SOAP endpoint", e);
            answer.setResult(false);
            answer.setResultMessage(e.getMessage());
        }

        return answer;
    }

    private List<String> persistAttachments(
            List<DomibusConnectorMessageAttachmentType> attachments) {
        if (attachments.isEmpty()) {
            return null;
        }

        var fileUploadCommands = toFileUploadCommands(attachments);

        try {
            return uploadAttachmentsService.execute(fileUploadCommands)
                                           .stream()
                                           .map(ConnectorMessageAttachment::identifier)
                                           .toList();
        } finally {
            fileUploadCommands.forEach(FileUploadCommand::cleanup);
        }
    }

    private String persistBusinessContent(DomibusConnectorMessageContentType businessContent) {
        var fileUploadCommand = toFileUploadCommand(businessContent);

        try {
            return uploadAttachmentsService.execute(List.of(fileUploadCommand))
                                           .getFirst()
                                           .identifier();
        } finally {
            fileUploadCommand.cleanup();
        }
    }

    private String persistBusinessDocument(DomibusConnectorMessageDocumentType pdfBusinessContent) {
        var fileUploadCommand = toFileUploadCommand(pdfBusinessContent);

        try {
            return uploadAttachmentsService.execute(List.of(fileUploadCommand))
                                           .getFirst()
                                           .identifier();
        } finally {
            fileUploadCommand.cleanup();
        }
    }

    private FileUploadCommand toFileUploadCommand(
            DomibusConnectorMessageDocumentType pdfBusinessContent) {
        try {
            var tempFile = AttachmentHelpers.dataHandlerToTempFile(
                    pdfBusinessContent.getDocument());
            var file = tempFile.toFile();
            var contentType = getContentType(tempFile);

            return FileUploadCommand
                    .builder()
                    .filename(StringUtils.cleanPath(pdfBusinessContent.getDocumentName()))
                    .contentType(contentType)
                    .size(file.length())
                    .tempFileLocation(tempFile)
                    .build();
        } catch (Exception e) {
            throw new ConnectorInternalServerException(e.getMessage());
        }
    }

    private FileUploadCommand toFileUploadCommand(
            DomibusConnectorMessageContentType businessContent) {
        try {
            var tempFile = AttachmentHelpers.sourceToTempFile(businessContent.getXmlContent());
            var file = tempFile.toFile();

            return FileUploadCommand
                    .builder()
                    .filename("businessContent.xml")
                    .contentType("text/xml")
                    .size(file.length())
                    .tempFileLocation(tempFile)
                    .build();
        } catch (Exception e) {
            throw new ConnectorInternalServerException(e.getMessage());
        }
    }

    private List<FileUploadCommand> toFileUploadCommands(
            List<DomibusConnectorMessageAttachmentType> attachments) {
        return attachments
                .stream()
                .map(attachment -> {
                         try {
                             var tempFile = AttachmentHelpers.dataHandlerToTempFile(
                                     attachment.getAttachment());
                             var file = tempFile.toFile();
                             return FileUploadCommand
                                     .builder()
                                     .filename(attachment.getName())
                                     .contentType(attachment.getMimeType())
                                     .size(file.length())
                                     .tempFileLocation(
                                             AttachmentHelpers.dataHandlerToTempFile(
                                                     attachment.getAttachment())
                                     )
                                     .build();
                         } catch (Exception e) {
                             throw new ConnectorInternalServerException(e.getMessage());
                         }
                     }
                )
                .toList();
    }

    private String getContentType(Path tempFile) throws IOException {
        var contentType = Files.probeContentType(tempFile);

        return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
