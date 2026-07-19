/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.transport;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStepStatus;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

/**
 * Data Transfer Object representing a step in the transport process of a connector message.
 *
 * <p>This record encapsulates information about a specific step within the connector's
 * message transport process, including identifying details, the status of the transport, and
 * lifecycle timestamps.
 *
 * @param identifier                   A unique identifier for the transport step.
 * @param remoteSystemIdentifier       Identifier representing the remote system involved in the
 *                                     transport step.
 * @param transportedMessageIdentifier A unique identifier for the message being transported during
 *                                     this step.
 * @param numberOfAttempts             The number of attempts made to execute this transport step.
 * @param status                       A textual representation of the current status of the
 *                                     transport step.
 * @param linkPartnerName              The name of the link partner involved in the transport step.
 * @param statuses                     A set of statuses corresponding to this transport step,
 *                                     detailing its state at specific points in time.
 * @param createdAt                    The timestamp indicating when this transport step was
 *                                     created.
 * @param messageType                  The type of message being transported in this step (BUSINESS
 *                                     or EVIDENCE message).
 * @param updatedAt                    The timestamp indicating when this transport step was last
 *                                     updated.
 */
@Builder
public record ConnectorMessageTransportStepDto(
    String identifier,
    String remoteSystemIdentifier,
    String transportedMessageIdentifier,
    int numberOfAttempts,
    String linkPartnerName,
    String status,
    Set<ConnectorMessageTransportStepStatusDto> statuses,
    String messageType,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Converts a {@link ConnectorMessageTransportStep} object into a
     * {@link ConnectorMessageTransportStepDto}.
     *
     * @param step The {@link ConnectorMessageTransportStep} instance to be converted.
     *
     * @return A {@link ConnectorMessageTransportStepDto} instance containing the data from the
     *     given {@link ConnectorMessageTransportStep}.
     */
    public static ConnectorMessageTransportStepDto from(ConnectorMessageTransportStep step) {
        return ConnectorMessageTransportStepDto
            .builder()
            .identifier(step.identifier())
            .remoteSystemIdentifier(step.remoteSystemIdentifier())
            .transportedMessageIdentifier(step.transportedMessageIdentifier())
            .numberOfAttempts(step.numberOfAttempts())
            .linkPartnerName(step.linkPartnerName())
            .status(step.status().name())
            .statuses(toStatuses(step.statuses()))
            .messageType(getTransportedMessageType(step.transportedMessage()))
            .createdAt(step.createdAt())
            .updatedAt(step.updatedAt())
            .build();
    }

    private static String getTransportedMessageType(ConnectorMessage transportedMessage) {
        return transportedMessage.isBusinessMessage() ? "BUSINESS" : "EVIDENCE";
    }

    private static Set<ConnectorMessageTransportStepStatusDto> toStatuses(
        Set<ConnectorMessageTransportStepStatus> statuses) {
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
