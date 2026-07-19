/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.pmode;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode.ConnectorProcessingModeDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.pmode.ConnectorProcessingModeCreationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines the API for managing processing modes within the connector system for administrative
 * purposes.
 *
 * <p>The base URI for the endpoints defined in this interface is:
 * {@code /api/v1/admin/processing-modes}.
 */
@Tag(
    name = "ProcessingModeAdmin",
    description = "API for managing processing modes for administrative purposes"
)
@RequestMapping("/api/v1/admin/processing-modes")
public interface ConnectorProcessingModeAdminApi {
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new processing mode.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ConnectorProcessingModeDto create(
        @RequestParam("processingModeXmlFile") MultipartFile processingModeXmlFile,
        @Valid @RequestPart("metadata") ConnectorProcessingModeCreationRequest metadata)
        throws IOException;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all processing modes.")
    List<ConnectorProcessingModeDto> listPmodes();

    @GetMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a processing mode by identifier.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pmode found"),
        @ApiResponse(responseCode = "404", description = "Not Found")
    })
    ConnectorProcessingModeDetailDto getPmode(@PathVariable String uuid);
}
