/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.entity.message;

import eu.ecodex.connector.domain.model.message.content.DetachedSignature;
import eu.ecodex.connector.domain.model.message.content.DetachedSignatureMimeType;
import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Represents the Connector Message Business Document Detached Signature entity used to manage and
 * store information about connector {@link DetachedSignature} within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_MESSAGE_BUSINESS_DOCUMENT_SIGNATURES")
public class ConnectorMessageBusinessDocumentDetachedSignatureEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "SIGNATURE", nullable = false)
    private byte[] signature;

    @Enumerated(EnumType.STRING)
    @Column(name = "MIME_TYPE", nullable = false)
    private DetachedSignatureMimeType mimeType;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "BUSINESS_DOCUMENT_ID", unique = true, nullable = false)
    private ConnectorMessageBusinessDocumentEntity businessDocument;
}
