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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for performing CRUD operations on the
 * {@link ConnectorMessageTransportStepEntity}.
 */
@Repository
public interface ConnectorMessageTransportStepJpaRepository extends
        JpaRepository<ConnectorMessageTransportStepEntity, Long>,
        JpaSpecificationExecutor<ConnectorMessageTransportStepEntity> {
    @EntityGraph(attributePaths = {"statuses"})
    ConnectorMessageTransportStepEntity findByIdentifier(String identifier);

    @Query(
            value = """
                    SELECT MTS.*
                    FROM connector_message_transport_steps MTS
                    WHERE MTS.transported_message_identifier = :identifier
                    OR MTS.remote_system_identifier = :identifier
                    """,
            nativeQuery = true
    )
    ConnectorMessageTransportStepEntity findByTransportedMessageIdentifierOrRemoteSystemIdentifier(
            @Param("identifier") String identifier);

    @Query(
            value = """
                    SELECT DISTINCT MTS.identifier
                    FROM connector_message_transport_steps MTS
                    WHERE MTS.status = 'PENDING'
                    AND MTS.link_partner_name = :backendName
                    """,
            nativeQuery = true
    )
    List<String> findAllPendingByBackendName(@Param("backendName") String backendName);

    @Query(
            value = """
                    SELECT DISTINCT MTS.transported_message_identifier
                    FROM connector_message_transport_steps MTS
                    WHERE MTS.status = 'PENDING'
                    AND MTS.link_partner_name = :backendName
                    """,
            nativeQuery = true
    )
    List<String> findAllPendingMessageIdsByBackendName(
            @Param("backendName") String backendName);

    @Modifying
    @Transactional
    @Query(
            value = """
                    UPDATE connector_message_transport_steps MTS
                    SET
                        MTS.status = :status,
                        MTS.number_of_attempts = MTS.number_of_attempts + 1,
                        MTS.updated_at = now()
                    WHERE MTS.identifier IN :identifiers
                    """,
            nativeQuery = true
    )
    void updateStatus(
            @Param("identifiers") List<String> identifiers,
            @Param("status") String status);
}
