/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for encapsulating evidence-related information in a
 * connector system.
 *
 * <p>This record contains essential information identifying the evidence message, which may
 * be used in various contexts such as logging, auditing, or data validation processes.
 *
 * @param identifier The unique identifier for the evidence message, which must not be blank.
 */
@Builder
public record ConnectorEvidenceMessageDto(
        @NotBlank String identifier
) {
    public static ConnectorEvidenceMessageDto of(String identifier) {
        return new ConnectorEvidenceMessageDto(identifier);
    }
}
