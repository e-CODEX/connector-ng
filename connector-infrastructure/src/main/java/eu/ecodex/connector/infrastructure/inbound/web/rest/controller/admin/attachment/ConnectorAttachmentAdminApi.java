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

import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorAttachmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Defines the API for managing message attachments for administrative purposes.
 *
 * <p>The base URI for the endpoints defined in this interface is:
 * {@code /api/v1/admin/attachments}.
 */
@RequestMapping("/api/v1/admin/attachments")
@Tag(
        name = "MessageAttachmentAdmin",
        description = "Defines the API for managing message attachments for administrative purposes"
)
public interface ConnectorAttachmentAdminApi {
    @GetMapping
    @Operation(summary = "Get paginated list of message attachments.")
    ConnectorPageResult<ConnectorAttachmentDto> listAttachments(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    );
}
