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

import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.infrastructure.persistence.entity.BaseEntity;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

/**
 * Represents the Connector Processing Mode Party entity used to manage and store information about
 * connector configurations within the system.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_PARTIES")
public class ConnectorPartyEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "IDENTIFIER_TYPE", nullable = false)
    private String identifierType;

    @Column(name = "ROLE", nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_TYPE", nullable = false)
    private ConnectorPartyRoleType roleType;

    @Column(name = "IS_HOME")
    private boolean isHome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROCESSING_MODE_ID", nullable = false)
    private ConnectorProcessingModeEntity processingMode;
}
