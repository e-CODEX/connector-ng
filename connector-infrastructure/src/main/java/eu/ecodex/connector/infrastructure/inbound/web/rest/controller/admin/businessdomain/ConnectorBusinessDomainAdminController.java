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

import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.service.usecase.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorBusinessDomainDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.request.ConnectorBusinessDomainCreationRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing business domains within the connector system.
 */
@RestController
public class ConnectorBusinessDomainAdminController implements ConnectorBusinessDomainAdminApi {
    private final ConnectorRegisterBusinessDomain registerBusinessDomain;
    private final ConnectorListBusinessDomain listBusinessDomain;

    public ConnectorBusinessDomainAdminController(
            ConnectorRegisterBusinessDomain registerBusinessDomain,
            ConnectorListBusinessDomain listBusinessDomain) {
        this.registerBusinessDomain = registerBusinessDomain;
        this.listBusinessDomain = listBusinessDomain;
    }


    @Override
    public ConnectorBusinessDomainDto create(
            @Valid @RequestBody ConnectorBusinessDomainCreationRequest request) {
        var created = this.registerBusinessDomain.execute(toDomain(request));

        return toDto(created);
    }

    @Override
    public List<ConnectorBusinessDomainDto> getAll() {
        var businessDomains = this.listBusinessDomain.execute();

        return businessDomains.stream().map(this::toDto).toList();
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
