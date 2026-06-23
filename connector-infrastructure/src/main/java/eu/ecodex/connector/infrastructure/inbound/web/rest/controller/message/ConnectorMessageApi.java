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

import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorOutboundMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDetailDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message.ConnectorMessageDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.message.ConnectorOutboundMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
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
 * Defines the API for managing connector messages.
 */
@Tag(name = "Message", description = "API for managing message.")
@RequestMapping("/api/v1/messages")
public interface ConnectorMessageApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/outbound", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit a message from the backend system to the connector")
    @ApiResponses(
            @ApiResponse(responseCode = "400", description = "Bad Request")
    )
    ConnectorOutboundMessageDto submitOutboundMessage(
            @RequestPart("businessXMLDocument") MultipartFile businessXMLDocument,
            @Valid @RequestPart("messageMetadata") ConnectorOutboundMessageRequest messageMetadata
    ) throws IOException;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get paginated list of messages.")
    ConnectorPageResult<ConnectorMessageDto> listMessages(
            @RequestParam(name = "identifier", required = false) String identifier,
            @RequestParam(name = "backendName", required = false) String backendName,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    );

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{identifier}")
    @Operation(summary = "Get a message by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message found"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    ConnectorMessageDetailDto retrieveMessage(@PathVariable("identifier") String identifier);
}
