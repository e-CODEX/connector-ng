/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.stats;

import eu.ecodex.connector.infrastructure.outbound.database.dto.ConnectorMessageStatsDto;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageEntity;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on the {@link ConnectorMessageEntity}.
 */
@Repository
@SuppressWarnings("checkstyle:LineLength")
public interface ConnectorMessageStatsJpaRepository extends
        JpaRepository<ConnectorMessageEntity, Long> {
    @Query("""
    SELECT new eu.ecodex.connector.infrastructure.outbound.database.dto.ConnectorMessageStatsDto(
        COUNT(m),
        COUNT(CASE WHEN m.deliveredToGatewayAt IS NOT NULL
                    OR m.deliveredToBackendAt IS NOT NULL THEN 1 END),
        COUNT(CASE WHEN m.rejectedAt IS NOT NULL THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.BACKEND_TO_GATEWAY
                    THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.BACKEND_TO_GATEWAY
                    AND m.deliveredToGatewayAt IS NOT NULL THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.BACKEND_TO_GATEWAY
                    AND m.rejectedAt IS NOT NULL THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.GATEWAY_TO_BACKEND
                    THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.GATEWAY_TO_BACKEND
                    AND m.deliveredToBackendAt IS NOT NULL THEN 1 END),
        COUNT(CASE WHEN m.direction = eu.ecodex.connector.domain.model.message.ConnectorMessageDirection.GATEWAY_TO_BACKEND
                    AND m.rejectedAt IS NOT NULL THEN 1 END))
    FROM ConnectorMessageEntity m
        WHERE (:from IS NULL OR m.createdAt >= :from)
              AND (:to   IS NULL OR m.createdAt <= :to)
    """)
    ConnectorMessageStatsDto computeStats(@Param("from") Instant from, @Param("to") Instant to);
}
