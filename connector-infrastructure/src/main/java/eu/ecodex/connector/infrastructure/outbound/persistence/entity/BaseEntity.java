/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import lombok.Getter;

/**
 * Represents a base entity with common properties for creation and update timestamps. This class is
 * designed to be extended by other entity classes that require these audit fields.
 *
 * <p>Fields:
 * <ul>
 *     <li> <b>createdAt</b>: Stores the timestamp for when the entity first persisted.
 *     <li> <b>updatedAt</b>: Stores the timestamp for the most recent update of the entity.
 * </ul>
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "CREATED_AT")
    private Instant createdAt;
    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
