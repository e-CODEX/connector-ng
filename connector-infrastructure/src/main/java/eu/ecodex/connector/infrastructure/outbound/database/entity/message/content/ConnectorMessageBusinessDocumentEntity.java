/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.entity.message.content;

import eu.ecodex.connector.domain.model.message.content.ConnectorBusinessDocumentAESType;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessDocument;
import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * Represents the Connector Message Business Content entity used to manage and store information
 * about connector {@link ConnectorMessageBusinessDocument} within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS")
public class ConnectorMessageBusinessDocumentEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "AES_TYPE", updatable = false)
    private ConnectorBusinessDocumentAESType aesType;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUSINESS_CONTENT_ID", unique = true, nullable = false)
    private ConnectorMessageBusinessContentEntity businessContent;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ATTACHMENT_ID", unique = true, nullable = false)
    private ConnectorMessageAttachmentEntity attachment;

    @Setter
    @OneToOne(mappedBy = "businessDocument")
    private ConnectorMessageBusinessDocumentDetachedSignatureEntity detachedSignature;
}
