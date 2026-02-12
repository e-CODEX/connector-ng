/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.persistence.entity.pmode;

import eu.ecodex.connector.infrastructure.persistence.entity.BaseEntity;
import eu.ecodex.connector.infrastructure.persistence.entity.ConnectorBusinessDomainEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * Represents the Connector Processing Mode entity used to manage and store information about
 * connector configurations within the system.
 */
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_PROCESSING_MODES")
public class ConnectorProcessingModeEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "FILENAME", nullable = false)
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUSINESS_DOMAIN_ID", nullable = false)
    private ConnectorBusinessDomainEntity businessDomain;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRUSTSTORE_ID")
    private ConnectorKeystoreEntity truststore;

    @Builder.Default
    @OneToMany(mappedBy = "processingMode", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectorPartyEntity> parties = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "processingMode", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectorActionEntity> actions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "processingMode", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectorServiceEntity> services = new HashSet<>();
}
