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

import eu.ecodex.connector.application.port.api.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.application.port.api.attachment.FileUploadCommand;
import eu.ecodex.connector.application.port.api.message.ConnectorListPendingMessageIds;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundBusinessMessageReceiver;
import eu.ecodex.connector.application.port.api.message.outbound.ConnectorOutboundEvidenceMessageReceiver;
import eu.ecodex.connector.application.port.api.transport.ConnectorAckMessageTransportStep;
import eu.ecodex.connector.application.port.api.transport.ConnectorRetrieveMessageByTransportId;
import eu.ecodex.connector.application.port.api.transport.ConnectorSetMessagesTransportStepToDownload;
import eu.ecodex.connector.application.port.api.transport.command.UpdateMessageTransportCommand;
import eu.ecodex.connector.application.service.message.ConnectorListPendingMessagesService;
import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.ecodex.connector.domain.transition.DomibusConnectorBackendWebService;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessagesType;
import eu.ecodex.connector.domain.transition.EmptyRequestType;
import eu.ecodex.connector.domain.transition.GetMessageByIdRequest;
import eu.ecodex.connector.domain.transition.ListPendingMessageIdsResponse;
import eu.ecodex.connector.infrastructure.helper.LegacyMessageHelper;
import eu.ecodex.connector.infrastructure.inbound.web.ConnectorBackendClientVerifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorInternalServerException;
import eu.ecodex.connector.infrastructure.inbound.web.soap.helper.AttachmentHelpers;
import eu.ecodex.connector.infrastructure.inbound.web.soap.helper.MessageHelpers;
import eu.ecodex.connector.infrastructure.inbound.web.soap.interceptor.ProcessMessageAfterDownload;
import eu.ecodex.connector.infrastructure.inbound.web.soap.interceptor.ProcessMessagesAfterDownload;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.MTOM;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.tika.Tika;
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
    private final ConnectorOutboundBusinessMessageReceiver businessMessageReceiver;
    private final ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageProcessor;
    private final ConnectorListPendingMessageIds listPendingMessageIdsService;
    private final ConnectorListPendingMessagesService listPendingMessagesService;
    private final ConnectorRetrieveMessageByTransportId retrieveMessageByTransportIdService;
    private final ConnectorUploadAttachments uploadAttachmentsService;
    private final ConnectorSetMessagesTransportStepToDownload changePendingMessagesStatusService;
    private final ConnectorAckMessageTransportStep ackMessageTransportStepService;
    private final ConnectorBackendClientVerifier backendClientVerifierService;
    private final LegacyMessageHelper legacyMessageHelper;
    private final Tika tika;
    private WebServiceContext webServiceContext;

    /**
     * Controller class for handling backend web service operations related to message
     * transportation, management, and interaction with external services.
     *
     * @param businessMessageReceiver             Service for handling outbound message processing
     *                                            before dispatch.
     * @param outboundEvidenceMessageProcessor    Service for handling outbound evidence message
     *                                            processing.
     * @param listPendingMessageIdsService        Service for retrieving a list of pending message
     *                                            IDs.
     * @param listPendingMessagesService          Service for retrieving a list of pending
     *                                            messages.
     * @param retrieveMessageByTransportIdService Service for retrieving a message by its transport
     *                                            ID.
     * @param uploadAttachmentsService            Service for handling the upload of message
     *                                            attachments.
     * @param changePendingMessagesStatusService  Service for changing the status of pending
     *                                            messages.
     * @param ackMessageTransportStepService      Service for updating message transport steps.
     * @param backendClientVerifierService        Service for verifying client requests directed to
     *                                            the backend.
     * @param legacyMessageHelper                 Helper for managing operations specific to legacy
     *                                            message formats.
     */
    public ConnectorBackendWebServiceController(
        ConnectorOutboundBusinessMessageReceiver businessMessageReceiver,
        ConnectorOutboundEvidenceMessageReceiver outboundEvidenceMessageProcessor,
        ConnectorListPendingMessageIds listPendingMessageIdsService,
        ConnectorListPendingMessagesService listPendingMessagesService,
        ConnectorRetrieveMessageByTransportId retrieveMessageByTransportIdService,
        ConnectorUploadAttachments uploadAttachmentsService,
        ConnectorSetMessagesTransportStepToDownload changePendingMessagesStatusService,
        ConnectorAckMessageTransportStep ackMessageTransportStepService,
        ConnectorBackendClientVerifier backendClientVerifierService,
        LegacyMessageHelper legacyMessageHelper) {
        this.businessMessageReceiver = businessMessageReceiver;
        this.outboundEvidenceMessageProcessor = outboundEvidenceMessageProcessor;
        this.listPendingMessageIdsService = listPendingMessageIdsService;
        this.listPendingMessagesService = listPendingMessagesService;
        this.retrieveMessageByTransportIdService = retrieveMessageByTransportIdService;
        this.uploadAttachmentsService = uploadAttachmentsService;
        this.changePendingMessagesStatusService = changePendingMessagesStatusService;
        this.ackMessageTransportStepService = ackMessageTransportStepService;
        this.backendClientVerifierService = backendClientVerifierService;
        this.legacyMessageHelper = legacyMessageHelper;
        this.tika = new Tika();
    }

    @Resource
    public void setWsContext(WebServiceContext webServiceContext) {
        this.webServiceContext = webServiceContext;
    }

    @Override
    public EmptyRequestType acknowledgeMessage(DomibusConnectorMessageResponseType responseType) {
        var commandBuilder = UpdateMessageTransportCommand
            .builder()
            .remoteMessageIdentifier(responseType.getAssignedMessageId());
        if (responseType.isResult()) {
            commandBuilder.status(ConnectorMessageTransportStatus.DELIVERED);
            commandBuilder.errors(null);
        } else {
            commandBuilder.status(ConnectorMessageTransportStatus.FAILED);
            commandBuilder.errors(toDomainErrors(responseType.getMessageErrors()));
        }

        ackMessageTransportStepService.execute(
            responseType.getResponseForMessageId(),
            commandBuilder.build()
        );

        return new EmptyRequestType();
    }

    @Override
    public ListPendingMessageIdsResponse listPendingMessageIds(
        EmptyRequestType listPendingMessageIdsRequest) {
        try {
            var backendClientName = getBackendClientName();
            var response = new ListPendingMessageIdsResponse();
            response.getMessageTransportIds().addAll(
                listPendingMessageIdsService.execute(backendClientName)
            );

            return response;
        } catch (Exception e) {
            log.error("Error listing pending messages from the connector via SOAP endpoint", e);

            throw e;
        }
    }

    @Override
    public DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest) {
        var transportIdentifier = getMessageByIdRequest.getMessageTransportId();
        var message = retrieveMessageByTransportIdService.execute(transportIdentifier);

        // add post invoke message processor
        var messageContext = webServiceContext.getMessageContext();
        var wrappedMessageContext = (WrappedMessageContext) messageContext;
        var interceptor = new ProcessMessageAfterDownload(
            message,
            ackMessageTransportStepService

        );
        wrappedMessageContext.getWrappedMessage().getInterceptorChain().add(interceptor);

        return this.legacyMessageHelper.convertMessage(message);
    }

    @Override
    public DomibusConnectorMessagesType requestMessages(EmptyRequestType requestMessagesRequest) {
        try {
            var backendClientName = getBackendClientName();
            var messages = this.listPendingMessagesService.execute(backendClientName);
            var legacyMessages = messages.stream()
                                         .map(this.legacyMessageHelper::convertMessage)
                                         .toList();

            var response = new DomibusConnectorMessagesType();
            response.getMessages().addAll(legacyMessages);

            // add post invoke message processor
            var messageContext = webServiceContext.getMessageContext();
            var wrappedMessageContext = (WrappedMessageContext) messageContext;
            var interceptor = new ProcessMessagesAfterDownload(
                backendClientName,
                changePendingMessagesStatusService

            );
            wrappedMessageContext.getWrappedMessage().getInterceptorChain().add(interceptor);

            return response;
        } catch (Exception e) {
            log.error("Error requesting messages from the connector via SOAP endpoint", e);

            throw e;
        }
    }

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(
        DomibusConnectorMessageType submitMessageRequest) {
        var answer = new DomibsConnectorAcknowledgementType();

        try {
            var backendClientName = getBackendClientName();

            if (MessageHelpers.isEvidenceTriggerRequest(submitMessageRequest)) {
                var evidenceMessageCommand = MessageHelpers.toEvidenceTriggerCommand(
                    submitMessageRequest,
                    backendClientName
                );
                var createdMessage = this.outboundEvidenceMessageProcessor.execute(
                    evidenceMessageCommand
                );
                answer.setMessageId(createdMessage.identifier());
            } else {
                var attachmentIdentifiers = persistAttachments(
                    submitMessageRequest.getMessageAttachments()
                );
                var businessContent = submitMessageRequest.getMessageContent();
                var businessDocumentAttachmentIdentifier = persistBusinessDocument(
                    businessContent.getDocument()
                );
                var businessContentAttachmentIdentifier = persistBusinessContent(businessContent);
                var parsedMessage = MessageHelpers.toBusinessCommand(
                    submitMessageRequest,
                    attachmentIdentifiers,
                    businessContentAttachmentIdentifier,
                    businessDocumentAttachmentIdentifier,
                    backendClientName
                );

                var createdMessage = this.businessMessageReceiver.execute(parsedMessage);
                answer.setMessageId(createdMessage.identifier());
            }

            answer.setResult(true);
        } catch (Exception e) {
            log.error("Error submitting message to the connector via SOAP endpoint", e);
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
                .description("Registered business document")
                .build();
        } catch (Exception e) {
            throw new ConnectorInternalServerException(e.getMessage());
        }
    }

    private FileUploadCommand toFileUploadCommand(
        DomibusConnectorMessageContentType businessContent) {
        try {
            var tempFile = AttachmentHelpers.sourceToTempFile(businessContent.getXmlContent());
            var filename = tempFile.getFileName().toString();
            var file = tempFile.toFile();

            return FileUploadCommand
                .builder()
                .filename(filename.endsWith("default.tmp") ? "businessContent.xml" : filename)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .size(file.length())
                .tempFileLocation(tempFile)
                .description("Registered business content")
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
                             .description("Registered attachment")
                             .build();
                     } catch (Exception e) {
                         throw new ConnectorInternalServerException(e.getMessage());
                     }
                 }
            )
            .toList();
    }

    private String getContentType(Path tempFile) throws IOException {
        var contentType = tika.detect(tempFile);

        return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private List<ConnectorMessageError> toDomainErrors(
        List<DomibusConnectorMessageErrorType> errors) {
        if (errors == null) {
            return new ArrayList<>();
        }

        return errors.stream().map(
            error -> ConnectorMessageError.builder()
                                          .label(error.getErrorMessage())
                                          .details(error.getErrorDetails())
                                          .source(error.getErrorSource())
                                          .build()).toList();
    }

    private String getBackendClientName() {
        var userPrincipal = webServiceContext.getUserPrincipal();
        var certificateDn = userPrincipal != null ? userPrincipal.getName() : null;

        if (userPrincipal == null || !StringUtils.hasLength(certificateDn)) {
            var error = String.format(
                "Cannot handle request because userPrincipal is [%s] the certificate DN "
                    + "is [%s].",
                userPrincipal,
                certificateDn
            );
            log.error(error);
            throw new IllegalStateException(error);
        }

        var backendClientName = this.backendClientVerifierService.getBackendClient(certificateDn);

        if (backendClientName == null) {
            var error = String.format(
                "Cannot handle request because the certificate DN [%s] is not registered as "
                    + "backend client.",
                certificateDn
            );
            log.error(error);
            throw new IllegalStateException(error);
        }

        log.debug("Link partner name is [{}]", backendClientName);

        return backendClientName;
    }
}
