/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.dto.message;

import eu.ecodex.connector.domain.model.message.evidence.ConnectorEvidenceType;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;

/**
 * Represents a Data Transfer Object (DTO) for message evidence in the connector domain.
 *
 * @param uuid                     The unique identifier of the evidence. May be null if the
 *                                 evidence has not been persisted or assigned an identifier.
 * @param type                     The type of the message evidence. This is defined using the
 *                                 {@link ConnectorEvidenceType} enumeration, which classifies
 *                                 evidence types based on priority, positivity, and occurrence
 *                                 constraints.
 * @param createdAt                The timestamp indicating when the evidence was created. May be
 *                                 null if the creation time is not recorded.
 * @param updatedAt                The timestamp indicating when the evidence was last updated. May
 *                                 be null if the evidence has never been updated.
 * @param deliveredToLinkPartnerAt The timestamp indicating when the evidence was delivered to the
 *                                 link partner. May be null if the delivery has not occurred or is
 *                                 not applicable.
 */
@Builder(toBuilder = true)
public record ConnectorMessageEvidenceDto(
    @Nullable String uuid,
    @Nonnull ConnectorEvidenceType type,
    @Nullable Instant createdAt,
    @Nullable Instant updatedAt,
    @Nullable Instant deliveredToLinkPartnerAt
) implements Serializable {
    /**
     * Converts a {@link ConnectorMessageEvidence} instance into a
     * {@link ConnectorMessageEvidenceDto} instance by mapping all relevant fields.
     *
     * @param evidence the source {@link ConnectorMessageEvidence} to be converted. This parameter
     *                 must not be null and should contain the evidence details.
     *
     * @return a {@link ConnectorMessageEvidenceDto} instance containing the mapped fields from the
     *     input evidence.
     */
    public static ConnectorMessageEvidenceDto from(ConnectorMessageEvidence evidence) {
        return ConnectorMessageEvidenceDto.builder()
                                          .uuid(evidence.uuid())
                                          .type(evidence.type())
                                          .createdAt(evidence.createdAt())
                                          .updatedAt(evidence.updatedAt())
                                          .deliveredToLinkPartnerAt(
                                              evidence.deliveredToLinkPartnerAt()
                                          )
                                          .build();
    }
}
