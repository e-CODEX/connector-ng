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

import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import java.time.Instant;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for a connector business domain.
 *
 * <p>It is typically used as a payload in administrative REST API operations for creating
 * or retrieving the state of business domains.
 *
 * @param uuid        A unique identifier for the business domain.
 * @param identifier  A logical identifier representing the business domain.
 * @param description A brief textual description of the business domain.
 * @param enabled     A boolean indicating whether the business domain is active or disabled.
 * @param source      The source of the configuration for the business domain, defined by the
 *                    {@link ConnectorConfigurationSource} enumeration.
 * @param createdAt   The timestamp when the business domain was created.
 * @param updatedAt   The timestamp when the business domain was last updated.
 */
@Builder
public record ConnectorBusinessDomainDto(
        String uuid,
        String identifier,
        String description, boolean enabled,
        ConnectorConfigurationSource source,
        Instant createdAt,
        Instant updatedAt
) {
}
