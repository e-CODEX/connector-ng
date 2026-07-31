/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.entity.user;

import eu.ecodex.connector.infrastructure.outbound.database.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
 * Represents a connector user entity in the identity and access management system.
 * This entity is responsible for persisting user data and their associated roles
 * in the database.
 *
 * <p>
 * Features:
 * - Each user has a unique identifier and universally unique UUID.
 * - User credentials include a username and password, both of which are required and unique.
 * - An optional email address can also be associated with the user.
 * - Indicates whether the user account is enabled or disabled via a boolean flag.
 * - Maintains roles associated with the user through a many-to-many relationship.
 *
 * <p>
 * Annotations:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table: Specifies the table "CONNECTOR_USERS" this entity is mapped to, along with indexes for
 * username and email.
 * - @Id: Indicates the primary key field of the entity.
 * - @GeneratedValue: Defines the primary key generation strategy.
 * - @UuidGenerator: Automatically generates a unique UUID for the entity.
 * - @ManyToMany: Establishes the many-to-many relationship with the ConnectorUserRoleEntity.
 * - @Builder, @Getter, @NoArgsConstructor, @AllArgsConstructor: Lombok annotations to simplify the
 * model.
 *
 * <p>
 * Table Mapping:
 * - Table name: CONNECTOR_USERS
 * - Indexes:
 * - IDX_CONNECTOR_USERS_EMAIL: Index on the "EMAIL" column.
 * - IDX_CONNECTOR_USERS_USERNAME: Index on the "USERNAME" column.
 * - IDX_CONNECTOR_USERS_UUID: Index on the "UUID" column.
 *
 * <p>
 * Relationships:
 * - Roles: Associated roles are managed in the "CONNECTOR_USERS_ROLES_ASSIGNMENTS" join table
 * using a many-to-many relationship with the ConnectorUserRoleEntity.
 */
@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_USERS",
        indexes = {
                @Index(name = "IDX_CONNECTOR_USERS_EMAIL", columnList = "EMAIL"),
                @Index(name = "IDX_CONNECTOR_USERS_USERNAME", columnList = "USERNAME"),
                @Index(name = "IDX_CONNECTOR_USERS_UUID", columnList = "UUID"),
        })
public class ConnectorUserEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Setter
    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @Setter
    @Column(name = "PASSWORD", unique = true, nullable = false)
    private String password;

    @Setter
    @Column(name = "EMAIL", unique = true)
    private String email;

    @Setter
    @Column(name = "ENABLED")
    private boolean enabled;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "CONNECTOR_USERS_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<ConnectorRoleEntity> roles = new HashSet<>();
}
