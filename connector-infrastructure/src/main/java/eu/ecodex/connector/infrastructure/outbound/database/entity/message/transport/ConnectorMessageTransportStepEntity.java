/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.entity.message.transport;

import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the Connector Message Transport Step entity used to manage and store information about
 * the connector {@link ConnectorMessageTransportStep} within the system.
 */
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_MESSAGE_TRANSPORT_STEPS")
public class ConnectorMessageTransportStepEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "NUMBER_OF_ATTEMPTS", nullable = false)
    private int numberOfAttempts;

    @Column(name = "TRANSPORTED_MESSAGE_IDENTIFIER", nullable = false)
    private String transportedMessageIdentifier;

    @Column(name = "REMOTE_SYSTEM_IDENTIFIER")
    private String remoteSystemIdentifier;

    @Size(max = 5_000_000)
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "TRANSPORTED_MESSAGE", length = 5_000_000, nullable = false)
    private String transportedMessage;

    @Column(name = "LINK_PARTNER_NAME")
    private String linkPartnerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private ConnectorMessageTransportStatus status;

    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "transportStep", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectorMessageTransportStepStatusEntity> statuses = new HashSet<>();
}
