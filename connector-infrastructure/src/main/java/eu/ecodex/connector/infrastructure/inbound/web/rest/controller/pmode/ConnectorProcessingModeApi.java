/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.pmode;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Defines the REST API for managing processing modes actions within the connector system.
 */
@Tag(
    name = "ProcessingMode",
    description = "API for managing processing modes"
)
@RequestMapping("/processing-modes")
public interface ConnectorProcessingModeApi {

    @GetMapping("{identifier}/services")
    @Operation(summary = "List a processing mode services")
    List<ConnectorService> listProcessingModeServices(@PathVariable String identifier);

    @GetMapping("{identifier}/actions")
    @Operation(summary = "List a processing mode actions")
    List<ConnectorAction> listProcessingModeActions(@PathVariable String identifier);

    @GetMapping("{identifier}/parties")
    @Operation(summary = "List a processing mode parties")
    List<ConnectorParty> listProcessingModeParties(@PathVariable String identifier);
}
