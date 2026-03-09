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

import eu.ecodex.connector.domain.model.message.ConnectorMessageAS4Properties;
import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorActionEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorPartyEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.pmode.ConnectorServiceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the Connector Message entity used to manage and store information about
 * connector {@link ConnectorMessageAS4Properties} within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_MESSAGE_AS4_PROPERTIES")
public class ConnectorMessageAS4PropertiesEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "EBMS_MESSAGE_IDENTIFIER")
    private String ebmsMessageIdentifier;

    @Column(name = "REFERENCE_TO_IDENTIFIER")
    private String referenceToIdentifier;

    @Column(name = "CONVERSATION_IDENTIFIER")
    private String conversationIdentifier;

    @Column(name = "ORIGINAL_SENDER", nullable = false)
    private String originalSender;

    @Column(name = "FINAL_RECIPIENT", nullable = false)
    private String finalRecipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private ConnectorServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_ID", nullable = false)
    private ConnectorActionEntity action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FROM_PARTY_ID", nullable = false)
    private ConnectorPartyEntity fromParty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TO_PARTY_ID", nullable = false)
    private ConnectorPartyEntity toParty;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MESSAGE_ID", unique = true, nullable = false)
    private ConnectorMessageEntity message;
}
