/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.service;

import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Defines the REST API for managing services.
 */
@Tag(
    name = "Services",
    description = "API for managing processing mode service."
)
@RequestMapping("/api/v1/services")
public interface ConnectorProcessingModeServiceApi {
    @GetMapping("")
    @Operation(summary = "List processing mode services belonging to a business domain")
    List<ConnectorService> listServices(
        @RequestParam(name = "businessDomain") String businessDomain);
}
