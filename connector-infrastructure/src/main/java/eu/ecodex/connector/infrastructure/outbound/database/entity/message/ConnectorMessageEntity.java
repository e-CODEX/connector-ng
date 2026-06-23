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

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.ConnectorBusinessDomainEntity;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.content.ConnectorMessageBusinessContentEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the Connector Message entity used to manage and store information about
 * connector {@link ConnectorMessage} within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_MESSAGES")
public class ConnectorMessageEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Setter
    @Column(name = "BACKEND_MESSAGE_IDENTIFIER")
    private String backendMessageIdentifier;

    @Setter
    @Column(name = "REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER")
    private String referenceToBackendMessageIdentifier;

    @Setter
    @Column(name = "BACKEND_NAME")
    private String backendName;

    @Setter
    @Column(name = "GATEWAY_NAME")
    private String gatewayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIRECTION", nullable = false)
    private ConnectorMessageDirection direction;

    @Column(name = "DELETED_AT")
    private Instant deletedAt;

    @Setter
    @Column(name = "REJECTED_AT")
    private Instant rejectedAt;

    @Setter
    @Column(name = "CONFIRMED_AT")
    private Instant confirmedAt;

    @Setter
    @Column(name = "DELIVERED_TO_GATEWAY_AT")
    private Instant deliveredToGatewayAt;

    @Setter
    @Column(name = "DELIVERED_TO_BACKEND_AT")
    private Instant deliveredToBackendAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUSINESS_DOMAIN_ID", nullable = false)
    private ConnectorBusinessDomainEntity businessDomain;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConnectorMessageBusinessContentEntity businessContent;

    @Setter
    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConnectorMessageAS4PropertiesEntity as4Properties;

    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorMessageAttachmentEntity> attachments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectorMessageEvidenceEntity> evidences = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorMessageErrorEntity> errors = new ArrayList<>();
}
