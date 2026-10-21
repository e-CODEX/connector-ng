/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.user;

import eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity.BaseEntity;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a refresh token entity used to manage and store information about connector.
 */
@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Table(
    name = "CONNECTOR_REFRESH_TOKENS",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_CRT_TOKEN", columnNames = "TOKEN")
    },
    indexes = {
        @Index(name = "IDX_CRT_USER_ID", columnList = "USER_ID")
    })
public class ConnectorRefreshTokenEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN", nullable = false, updatable = false)
    private String token;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Instant expiresAt;

    @Column(name = "REVOKED", nullable = false)
    private boolean revoked;

    @Column(name = "REVOKED_AT")
    private Instant revokedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "USER_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_CRT_USER"))
    private ConnectorUserEntity user;
}

