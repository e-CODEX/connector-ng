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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing an outbound {@link ConnectorMessage}.
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
}
