/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.pmode;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.pmode.ConnectorService;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for the processing mode configurations of the connector
 * system.
 *
 * @param uuid                     A unique identifier for the processing mode.
 * @param description              A brief textual description of the processing mode.
 * @param content                  The raw (XML) businessContent or configuration details of the
 *                                 processing mode.
 * @param filename                 The filename of the processing mode configuration file.
 * @param businessDomainIdentifier A logical identifier representing the business domain associated
 *                                 with the processing mode.
 * @param truststore               The truststore information associated with the processing mode.
 * @param parties                  The set of {@link ConnectorParty}s involved in the processing
 *                                 mode.
 * @param services                 The set of {@link ConnectorService}s involved in the processing
 *                                 mode.
 * @param actions                  The set of {@link ConnectorAction}s associated with the
 *                                 processing mode.
 * @param createdAt                The timestamp when the processing mode was created.
 * @param updatedAt                The timestamp when the processing mode was last updated.
 */
@Builder
public record ConnectorProcessingModeDetailDto(
    String uuid,
    String description,
    String content,
    String filename,
    String businessDomainIdentifier,
    ConnectorBusinessDomain businessDomain,
    ConnectorProcessingModeTruststoreDto truststore,
    Set<ConnectorParty> parties,
    Set<ConnectorService> services,
    Set<ConnectorAction> actions,
    Instant createdAt,
    Instant updatedAt
) {
    /**
     * Converts a {@link ConnectorProcessingMode} object into a
     * {@link ConnectorProcessingModeDetailDto}.
     *
     * @param processingMode The {@link ConnectorProcessingMode} to be converted. Must not be null.
     *
     * @return A {@link ConnectorProcessingModeDetailDto} representing the details of the provided
     *     processing mode.
     */
    public static ConnectorProcessingModeDetailDto from(ConnectorProcessingMode processingMode) {
        return ConnectorProcessingModeDetailDto
            .builder()
            .uuid(processingMode.uuid())
            .description(processingMode.description())
            .content(processingMode.content())
            .filename(processingMode.filename())
            .businessDomainIdentifier(
                Objects.requireNonNull(processingMode.businessDomain())
                       .identifier().messageLaneIdentifier()
            )
            .truststore(ConnectorProcessingModeTruststoreDto.from(processingMode.truststore()))
            .parties(processingMode.parties())
            .services(processingMode.services())
            .actions(processingMode.actions())
            .createdAt(processingMode.createdAt())
            .updatedAt(processingMode.updatedAt())
            .build();
    }
}
