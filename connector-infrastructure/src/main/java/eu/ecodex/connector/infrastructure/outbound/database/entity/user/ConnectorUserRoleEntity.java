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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
 * Represents a user role entity within the connector's identity and access management system.
 * This entity is used to define roles that can be assigned to users and persists the mapping
 * between roles and users in the database.
 * <p>
 * Features:
 * - Each role entity has a unique identifier and a unique role name.
 * - Roles are associated with users via a many-to-many relationship.
 * - The class is extensible from the BaseEntity to inherit creation and update timestamp fields.
 * <p>
 * Annotations used:
 * - @Entity: Specifies that this class is a JPA entity.
 * - @Table: Maps the entity to the "CONNECTOR_USERS_ROLES" table in the database.
 * - @Id and @GeneratedValue: Define the primary key and its generation strategy.
 * - @ManyToMany: Establishes a bidirectional many-to-many relationship with the ConnectorUserEntity class.
 * - @Builder, @Getter, @NoArgsConstructor, @AllArgsConstructor: Lombok annotations for simplifying the class structure.
 */
@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONNECTOR_USERS_ROLES",
        indexes = {
                @Index(name = "IDX_CONNECTOR_USERS_ROLES_UUID", columnList = "UUID"),
        })
public class ConnectorUserRoleEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @UuidGenerator
    @Column(name = "UUID", unique = true, nullable = false, updatable = false)
    private String uuid;

    @Setter
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<ConnectorUserEntity> users = new HashSet<>();
}
