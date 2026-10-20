/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.BaseEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeActionEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModePartyEntity;
import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.pmode.ConnectorProcessingModeServiceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the Connector Message entity used to manage and store information about connector
 * {@link ConnectorMessageAS4Properties} within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "CONNECTOR_MESSAGE_AS4_PROPERTIES",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_CM_AS4_MESSAGE", columnNames = "MESSAGE_ID")
    },
    indexes = {
        @Index(name = "IDX_CM_AS4_EBMS_MSG_ID", columnList = "EBMS_MESSAGE_IDENTIFIER"),
        @Index(name = "IDX_CM_AS4_REF_TO_ID", columnList = "REFERENCE_TO_IDENTIFIER"),
        @Index(name = "IDX_CM_AS4_CONVERSATION_ID", columnList = "CONVERSATION_IDENTIFIER"),
        @Index(name = "IDX_CM_AS4_ORIG_SENDER", columnList = "ORIGINAL_SENDER"),
        @Index(name = "IDX_CM_AS4_FINAL_RECIPIENT", columnList = "FINAL_RECIPIENT"),
        @Index(name = "IDX_CM_AS4_SERVICE_ID", columnList = "SERVICE_ID"),
        @Index(name = "IDX_CM_AS4_ACTION_ID", columnList = "ACTION_ID"),
        @Index(name = "IDX_CM_AS4_FROM_PARTY_ID", columnList = "FROM_PARTY_ID"),
        @Index(name = "IDX_CM_AS4_TO_PARTY_ID", columnList = "TO_PARTY_ID")
    }
)
public class ConnectorMessageAS4PropertiesEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "EBMS_MESSAGE_IDENTIFIER")
    private String ebmsMessageIdentifier;

    @Column(name = "REFERENCE_TO_IDENTIFIER", updatable = false)
    private String referenceToIdentifier;

    @Column(name = "CONVERSATION_IDENTIFIER", updatable = false)
    private String conversationIdentifier;

    @Column(name = "ORIGINAL_SENDER", nullable = false)
    private String originalSender;

    @Column(name = "FINAL_RECIPIENT", nullable = false)
    private String finalRecipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "SERVICE_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CM_AS4_SERVICE")
    )
    private ConnectorProcessingModeServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "ACTION_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CM_AS4_ACTION")
    )
    private ConnectorProcessingModeActionEntity action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "FROM_PARTY_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CM_AS4_FROM_PARTY")
    )
    private ConnectorProcessingModePartyEntity fromParty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "TO_PARTY_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CM_AS4_TO_PARTY")
    )
    private ConnectorProcessingModePartyEntity toParty;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "MESSAGE_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CM_AS4_MESSAGE")
    )
    private ConnectorMessageEntity message;
}
