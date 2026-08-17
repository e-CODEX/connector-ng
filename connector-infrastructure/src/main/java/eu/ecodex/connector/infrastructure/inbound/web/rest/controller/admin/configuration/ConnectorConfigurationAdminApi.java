/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.configuration;

import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorBusinessDocumentPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorBusinessDomainPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorContainerPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorEvidencesPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorLinkPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorMessageProcessingPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorMessageRoutingPropertiesDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.configuration.ConnectorQueuePropertiesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Defines the API for managing configurations within the connector system for administrative
 * purposes.
 */
@Tag(
    name = "ConfigurationAdmin",
    description = "API for managing configuration within the connector system for "
        + "administrative purposes."
)
@RequestMapping(value = "/api/v1/admin/configurations")
@PreAuthorize("hasRole('ADMIN')")
public interface ConnectorConfigurationAdminApi {
    @GetMapping("/business-domains")
    @Operation(summary = "List business domain configurations")
    ConnectorBusinessDomainPropertiesDto listBusinessDomains();

    @GetMapping("/container")
    @Operation(summary = "List container configurations")
    ConnectorContainerPropertiesDto listContainer();

    @GetMapping("/queues")
    @Operation(summary = "List queues configurations")
    ConnectorQueuePropertiesDto listQueues();

    @GetMapping("/message-processing")
    @Operation(summary = "List message processing configurations")
    ConnectorMessageProcessingPropertiesDto listMessageProcessing();

    @GetMapping("/evidence")
    @Operation(summary = "List evidences configurations")
    ConnectorEvidencesPropertiesDto listEvidences();

    @GetMapping("/business-document")
    @Operation(summary = "List business document configurations")
    ConnectorBusinessDocumentPropertiesDto listBusinessDocument();

    @GetMapping("/routing")
    @Operation(summary = "List routing configurations")
    ConnectorMessageRoutingPropertiesDto listRouting();

    @GetMapping("/backend-link-partners")
    @Operation(summary = "List backend link partners configurations")
    ConnectorLinkPropertiesDto listBackendLinkPartners();
}
