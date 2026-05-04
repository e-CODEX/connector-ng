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
 * Contains Spring Data JPA repositories for database persistence.
 *
 * <p>This package defines repository interfaces used by the infrastructure layer to access and
 * manage persisted connector data, such as parties, services, actions, evidences, keystores,
 * processing modes, and business domains.
 *
 * <p>Repositories in this package are responsible for database access only. Higher layers should
 * interact with persistence through application or domain-facing abstractions where applicable,
 * keeping database-specific concerns isolated within the infrastructure layer.
 */
package eu.ecodex.connector.infrastructure.outbound.database.repository;
