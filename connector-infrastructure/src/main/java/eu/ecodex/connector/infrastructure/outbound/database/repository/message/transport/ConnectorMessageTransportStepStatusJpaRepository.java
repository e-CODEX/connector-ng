/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.message.transport;

import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepStatusEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for performing CRUD operations on the
 * {@link ConnectorMessageTransportStepStatusEntity}.
 */
@Repository
public interface ConnectorMessageTransportStepStatusJpaRepository extends
        JpaRepository<ConnectorMessageTransportStepStatusEntity, Long> {
    @Modifying
    @Transactional
    @Query(
            value = """
                    INSERT INTO connector_message_transport_step_statuses(
                        status, transport_step_id, created_at, updated_at
                    )
                    SELECT :status, MTS.id, now(), now()
                    FROM connector_message_transport_steps MTS
                    WHERE MTS.identifier IN :identifiers
                    """,
            nativeQuery = true
    )
    void insert(@Param("identifiers") List<String> identifiers, @Param("status") String status);
}
