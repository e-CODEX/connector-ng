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

import eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport.ConnectorMessageTransportStepEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on the
 * {@link ConnectorMessageTransportStepEntity}.
 */
@Repository
public interface ConnectorMessageTransportStepJpaRepository extends
        JpaRepository<ConnectorMessageTransportStepEntity, Long> {
    ConnectorMessageTransportStepEntity findByIdentifier(String identifier);

    ConnectorMessageTransportStepEntity findByMessageIdentifier(String messageIdentifier);

    @Query(
            value = """
                    SELECT DISTINCT MTS.identifier
                    FROM connector_message_transport_steps MTS, connector_messages MSG
                    WHERE MTS.message_id = MSG.id
                    AND MTS.status = 'PENDING'
                    AND MSG.backend_name = :backendName
                    """,
            nativeQuery = true
    )
    List<String> findAllPendingByMessageBackendName(@Param("backendName") String backendName);
}
