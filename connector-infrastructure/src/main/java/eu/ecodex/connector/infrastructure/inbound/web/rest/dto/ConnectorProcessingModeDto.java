/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.controller.rest.dto;

import java.time.Instant;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for the processing mode configurations of the connector
 * system.
 *
 * @param uuid                     A unique identifier for the processing mode.
 * @param description              A brief textual description of the processing mode.
 * @param content                  The raw (XML) content or configuration details of the processing
 *                                 mode.
 * @param filename                 The filename of the processing mode configuration file.
 * @param businessDomainIdentifier A logical identifier representing the business domain associated
 *                                 with the processing mode.
 * @param createdAt                The timestamp when the processing mode was created.
 * @param updatedAt                The timestamp when the processing mode was last updated.
 */
@Builder
public record ConnectorProcessingModeDto(
        String uuid,
        String description,
        String content,
        String filename,
        String businessDomainIdentifier,
        Instant createdAt,
        Instant updatedAt
) {
}
