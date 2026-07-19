/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.attachment;

import eu.ecodex.connector.application.port.api.attachment.ConnectorListAttachments;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorAttachmentDto;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing large files within the connector system.
 */
@RestController
public class ConnectorAttachmentAdminController implements ConnectorAttachmentAdminApi {
    private final ConnectorListAttachments listAttachmentsService;

    public ConnectorAttachmentAdminController(
        ConnectorListAttachments listAttachmentsService) {
        this.listAttachmentsService = listAttachmentsService;
    }

    @Override
    public ConnectorPageResult<ConnectorAttachmentDto> listAttachments(int page, int size) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);

        var attachments = listAttachmentsService.execute(pageRequest);

        return ConnectorPageResult.of(
            attachments.content().stream().map(ConnectorAttachmentDto::from).toList(),
            attachments.size(),
            attachments.totalElements(),
            attachments.totalPages()
        );
    }
}
