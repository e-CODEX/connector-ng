/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.businessdomain;

import eu.ecodex.connector.infrastructure.inbound.web.rest.advice.ErrorResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorBusinessDomainDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.ConnectorBusinessDomainCreationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines the API for managing business domains within the connector system for administrative
 * purposes.
 *
 * <p>This interface provides operations for performing administrative tasks related to business
 * domains. The API is intended to be used by system administrators to manage domain-specific
 * settings.
 *
 * <p>The base URI for the endpoints defined in this interface is:
 * {@code /api/v1/admin/business-domains}.
 */
@Tag(name = "BusinessDomainsAdministration", description = "API for managing business domains.")
@RequestMapping(value = "/api/v1/admin/business-domains")
public interface ConnectorBusinessDomainAdminApi {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new business domain.")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    )
    ConnectorBusinessDomainDto create(
            @Valid @RequestBody ConnectorBusinessDomainCreationRequest request);

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all business domains.")
    List<ConnectorBusinessDomainDto> getAll();
}
