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
 * Provides database-backed outbound infrastructure components.
 *
 * <p>This package contains the persistence-facing implementation details used by the
 * connector infrastructure to store, retrieve, and manage data. It groups database entities,
 * repositories, and related persistence components.
 *
 * <p>Classes in this package belong to the infrastructure layer and should expose
 * persistence concerns through application or domain-facing abstractions rather than leaking
 * database-specific details into higher layers.
 */
package eu.ecodex.connector.infrastructure.outbound.database;
