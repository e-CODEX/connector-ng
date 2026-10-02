/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing an outbound {@link ConnectorBusinessMessage}.
 *
 * <p>This DTO is used to transfer message metadata between layers, services, or external
 * components. It includes identifiers, message direction, and an optional reference to a related
 * backend message.
 *
 * @param identifier                          the unique identifier of the outbound message
 * @param backendMessageIdentifier            the identifier assigned by the backend system
 * @param referenceToBackendMessageIdentifier optional reference to a related backend message;
 * @param direction                           the message direction
 */
@Builder
public record ConnectorOutboundMessageDto(
    @NotBlank String identifier,
    @NotBlank String backendMessageIdentifier,
    @Nullable String referenceToBackendMessageIdentifier,
    @Nonnull ConnectorMessageDirection direction
) {
    /**
     * Creates an outbound message DTO from a connector business message.
     *
     * @param message the connector business message to convert
     *
     * @return a DTO representing the outbound message
     *
     * @throws NullPointerException if the message direction is {@code null}
     */
    public static ConnectorOutboundMessageDto from(ConnectorBusinessMessage message) {
        return ConnectorOutboundMessageDto
            .builder()
            .identifier(message.identifier())
            .backendMessageIdentifier(message.backendMessageIdentifier())
            .referenceToBackendMessageIdentifier(message.referenceToBackendMessageIdentifier())
            .direction(Objects.requireNonNull(message.direction()))
            .build();
    }
}
