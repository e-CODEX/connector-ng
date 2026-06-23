/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.transport;

import eu.ecodex.connector.application.service.usecase.transport.ConnectorListTransportSteps;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStepStatus;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import eu.ecodex.connector.domain.model.paging.SortDirection;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepDto;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport.ConnectorMessageTransportStepStatusDto;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RestController;

/**
 * Defines the REST controller for managing transport steps within the connector system.
 */
@RestController
public class ConnectorMessageTransportStepController implements ConnectorMessageTransportStepApi {
    private final ConnectorListTransportSteps listTransportStepsService;

    public ConnectorMessageTransportStepController(
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
                transportSteps.content().stream().map(this::toDto).toList(),
                transportSteps.size(),
                transportSteps.totalElements(),
                transportSteps.totalPages()
        );
    }

    private ConnectorMessageTransportStepDto toDto(ConnectorMessageTransportStep transportStep) {
        if (transportStep == null) {
            return null;
        }

        return ConnectorMessageTransportStepDto
                .builder()
                .identifier(transportStep.identifier())
                .remoteSystemIdentifier(transportStep.remoteSystemIdentifier())
                .transportedMessageIdentifier(transportStep.transportedMessageIdentifier())
                .numberOfAttempts(transportStep.numberOfAttempts())
                .linkPartnerName(transportStep.linkPartnerName())
                .status(transportStep.status().name())
                .statuses(toStatuses(transportStep.statuses()))
                .messageType(getTransportedMessageType(transportStep.transportedMessage()))
                .createdAt(transportStep.createdAt())
                .updatedAt(transportStep.updatedAt())
                .build();
    }

    private String getTransportedMessageType(ConnectorMessage transportedMessage) {
        return transportedMessage.isBusinessMessage() ? "BUSINESS" : "EVIDENCE";
    }

    private Set<ConnectorMessageTransportStepStatusDto>
    toStatuses(Set<ConnectorMessageTransportStepStatus> statuses) {
        return statuses.stream()
                       .map(status ->
                                    ConnectorMessageTransportStepStatusDto
                                            .builder()
                                            .status(status.status().name())
                                            .createdAt(status.createdAt())
                                            .build())
                       .collect(Collectors.toSet());
    }
}
