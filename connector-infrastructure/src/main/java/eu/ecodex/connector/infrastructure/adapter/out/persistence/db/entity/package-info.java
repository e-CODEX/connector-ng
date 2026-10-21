/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/**
 * JPA entities mapping the connector's database tables.
 *
 * <p>Classes in this package describe how data is stored: tables, columns, relationships and
 * constraints. They are persistence representations, distinct from the domain model of
 * {@link eu.ecodex.connector.domain.model}, and are converted to and from domain types by the port
 * implementations in {@link eu.ecodex.connector.infrastructure.adapter.out.persistence.adapter}.
 *
 * <p>Entities are internal to the persistence adapter: they are accessed through the
 * repositories of {@code db.repository}, never exposed to the application or domain layers, and
 * contain no business rules. The schema they map is versioned with Liquibase.
 */
package eu.ecodex.connector.infrastructure.adapter.out.persistence.db.entity;
