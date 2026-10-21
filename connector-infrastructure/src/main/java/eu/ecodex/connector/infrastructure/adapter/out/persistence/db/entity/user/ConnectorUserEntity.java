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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * Represents a connector user entity in the identity and access management system. This entity is
 * responsible for persisting user data and their associated roles in the database.
 */
@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "CONNECTOR_USERS",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_CU_UUID", columnNames = "UUID"),
        @UniqueConstraint(name = "UQ_CU_USERNAME", columnNames = "USERNAME")
    }
)
public class ConnectorUserEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @UuidGenerator
    @Column(name = "UUID", nullable = false, updatable = false)
    private String uuid;

    @Setter
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Setter
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Setter
    @Column(name = "EMAIL")
    private String email;

    @Setter
    @Column(name = "ENABLED")
    private boolean enabled;

    @Setter
    @Column(name = "MUST_CHANGE_PASSWORD")
    private boolean mustChangePassword;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "CONNECTOR_USERS_ROLES",
        joinColumns = @JoinColumn(
            name = "USER_ID",
            foreignKey = @ForeignKey(name = "FK_CUR_USER")),
        inverseJoinColumns = @JoinColumn(
            name = "ROLE_ID",
            foreignKey = @ForeignKey(name = "FK_CUR_ROLE")),
        uniqueConstraints = {
            @UniqueConstraint(name = "UQ_CUR_USER_ROLE", columnNames = {"USER_ID", "ROLE_ID"})
        }
    )
    private Set<ConnectorRoleEntity> roles = new HashSet<>();

    /**
     * Returns the immutable roles associated with the user.
     *
     * @return roles
     */
    public Set<ConnectorRoleEntity> getRoles() {
        return roles == null ? null : Collections.unmodifiableSet(roles);
    }

    /**
     * Sets the roles associated with the user. This method replaces any existing roles with the
     * provided set. If the provided set is null, it clears the existing roles.
     *
     * @param roles roles to set
     */
    public void setRoles(Set<ConnectorRoleEntity> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }
}
