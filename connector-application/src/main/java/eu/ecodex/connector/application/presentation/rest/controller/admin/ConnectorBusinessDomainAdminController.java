/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.presentation.rest.controller.admin;

import eu.ecodex.connector.application.presentation.rest.api.admin.ConnectorBusinessDomainAdminApi;
import eu.ecodex.connector.application.presentation.rest.dto.ConnectorBusinessDomainDto;
import eu.ecodex.connector.application.presentation.rest.request.ConnectorBusinessDomainCreationRequest;
import eu.ecodex.connector.domain.api.service.ConnectorBusinessDomainService;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing business domains within the connector system.
 */
@RestController
public class ConnectorBusinessDomainAdminController implements ConnectorBusinessDomainAdminApi {
    private final ConnectorBusinessDomainService connectorBusinessDomainService;

    public ConnectorBusinessDomainAdminController(
            ConnectorBusinessDomainService connectorBusinessDomainService) {
        this.connectorBusinessDomainService = connectorBusinessDomainService;
    }

    @Override
    public ConnectorBusinessDomainDto create(
            @Valid @RequestBody ConnectorBusinessDomainCreationRequest request) {
        var created = this.connectorBusinessDomainService.register(toDomain(request));

        return toDto(created);
    }

    private ConnectorBusinessDomainDto toDto(ConnectorBusinessDomain businessDomain) {
        return ConnectorBusinessDomainDto.builder()
                                         .uuid(businessDomain.uuid())
                                         .identifier(
                                                 businessDomain.identifier().messageLaneIdentifier()
                                         )
                                         .description(businessDomain.description())
                                         .enabled(businessDomain.enabled())
                                         .source(businessDomain.source())
                                         .createdAt(businessDomain.createdAt())
                                         .updatedAt(businessDomain.updatedAt())
                                         .build();
    }

    private ConnectorBusinessDomain toDomain(ConnectorBusinessDomainCreationRequest request) {
        return ConnectorBusinessDomain.builder()
                                      .identifier(
                                              ConnectorBusinessDomainIdentifier
                                                      .builder()
                                                      .messageLaneIdentifier(request.identifier())
                                                      .build()
                                      )
                                      .description(request.description())
                                      .source(request.source())
                                      .build();
    }
}
