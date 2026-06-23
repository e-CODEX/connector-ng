/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.attachement;

import eu.ecodex.connector.application.service.impl.attachement.FileUploadCommand;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorListAttachments;
import eu.ecodex.connector.application.service.usecase.attachment.ConnectorUploadAttachments;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorAttachmentDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.exception.ConnectorInternalServerException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the REST controller for managing large files within the connector system.
 */
@RestController
public class ConnectorAttachmentController implements ConnectorAttachmentApi {
    private final ConnectorUploadAttachments uploadAttachmentsService;
    private final ConnectorListAttachments listAttachmentsService;

    public ConnectorAttachmentController(
            ConnectorUploadAttachments uploadAttachmentsService,
            ConnectorListAttachments listAttachmentsService) {
        this.uploadAttachmentsService = uploadAttachmentsService;
        this.listAttachmentsService = listAttachmentsService;
    }

    @Override
    public List<String> upload(List<MultipartFile> attachments) {
        var fileUploadCommands = attachments
                .stream()
                .map(attachment -> {
                         try {
                             var tempPath = Files.createTempFile(
                                     "upload_", attachment.getOriginalFilename()
                             );
                             attachment.transferTo(tempPath);

                             return new FileUploadCommand(
                                     attachment.getOriginalFilename(),
                                     attachment.getSize(),
                                     attachment.getContentType(),
                                     tempPath
                             );
                         } catch (IOException e) {
                             throw new ConnectorInternalServerException(e.getMessage());
                         }
                     }
                ).toList();

        try {
            return this.uploadAttachmentsService.execute(fileUploadCommands)
                                                .stream()
                                                .map(ConnectorMessageAttachment::identifier)
                                                .toList();
        } finally {
            fileUploadCommands.forEach(FileUploadCommand::cleanup);
        }
    }

    @Override
    public ConnectorPageResult<ConnectorAttachmentDto> listAttachments(int page, int size) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);

        var attachments = listAttachmentsService.execute(pageRequest);

        return ConnectorPageResult.of(
                attachments.content().stream().map(this::toDto).toList(),
                attachments.size(),
                attachments.totalElements(),
                attachments.totalPages()
        );
    }

    private ConnectorAttachmentDto toDto(ConnectorMessageAttachment attachment) {
        return ConnectorAttachmentDto
                .builder()
                .identifier(attachment.identifier())
                .name(attachment.name())
                .size(attachment.size())
                .contentType(attachment.contentType())
                .description(attachment.description())
                .storage(attachment.storage())
                .type(attachment.type())
                .createdAt(attachment.createdAt())
                .updatedAt(attachment.updatedAt())
                .build();
    }
}
