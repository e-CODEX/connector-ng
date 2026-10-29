/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.db.repository.message;

import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.message.ConnectorMessageAttachmentEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on the
 * {@link ConnectorMessageAttachmentEntity}.
 */
@Repository
public interface ConnectorMessageAttachmentJpaRepository extends
    JpaRepository<ConnectorMessageAttachmentEntity, Long>,
    JpaSpecificationExecutor<ConnectorMessageAttachmentEntity> {
    @EntityGraph(attributePaths = {"message"})
    ConnectorMessageAttachmentEntity findByIdentifier(String identifier);

    @Query(value = """
        SELECT CMA.*
        FROM connector_message_attachments CMA
        WHERE CMA.message_id IS NULL
        AND CMA.deleted_at IS NULL
        AND CMA.created_at <= :threshold
        """,
        nativeQuery = true
    )
    List<ConnectorMessageAttachmentEntity> findOrphansToDelete(
        @Param("threshold") Instant threshold);

    List<ConnectorMessageAttachmentEntity> findAllByMessageIdentifierAndDeletedAtIsNull(
        @Param("messageIdentifier") String messageIdentifier);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE connector_message_attachments CMA
           SET CMA.deleted_at = :deletedAt
         WHERE CMA.identifier IN (:identifiers)
           AND CMA.deleted_at IS NULL
        """, nativeQuery = true)
    int markAsDeleted(
        @Param("identifiers") List<String> identifiers,
        @Param("deletedAt") Instant deletedAt);
}
