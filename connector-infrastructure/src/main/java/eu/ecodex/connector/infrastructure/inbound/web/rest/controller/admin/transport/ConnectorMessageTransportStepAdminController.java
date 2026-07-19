/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.transport;

import eu.ecodex.connector.application.port.api.transport.ConnectorListTransportSteps;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing transport steps within the connector system.
 */
@RestController
public class ConnectorMessageTransportStepAdminController
    implements ConnectorMessageTransportStepAdminApi {
    private final ConnectorListTransportSteps listTransportStepsService;

    public ConnectorMessageTransportStepAdminController(
        ConnectorListTransportSteps listTransportStepsService) {
        this.listTransportStepsService = listTransportStepsService;
    }

    @Override
    public ConnectorPageResult<ConnectorMessageTransportStepDto> listTransportSteps(
        String messageOrRemoteSystemIdentifier,
        String linkPartnerName,
        int page,
        int size) {
        var pageRequest = ConnectorPageRequest.of(page, size, "createdAt", SortDirection.DESC);
        var transportSteps = listTransportStepsService.execute(
            pageRequest,
            messageOrRemoteSystemIdentifier,
            linkPartnerName
        );

        return ConnectorPageResult.of(
            transportSteps.content()
                          .stream()
                          .map(ConnectorMessageTransportStepDto::from)
                          .toList(),
            transportSteps.size(),
            transportSteps.totalElements(),
            transportSteps.totalPages()
        );
    }
}
