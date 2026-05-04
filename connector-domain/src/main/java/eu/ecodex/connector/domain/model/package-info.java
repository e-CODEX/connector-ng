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
 * Contains the core domain model of the e-CODEX Connector.
 *
 * <p>This package defines the business entities and value objects used across the connector
 * application. It represents the central data structures that model the system's domain and are
 * independent of technical concerns such as persistence or transport layers.
 *
 * <p>Typical classes in this package include:
 * <ul>
 *   <li>Domain entities representing core business concepts
 *   <li>Value objects used for immutable data representation
 *   <li>Enumerations defining domain-specific constants
 * </ul>
 *
 * <p>The domain model should remain stable and free of framework-specific
 * dependencies to ensure maintainability and portability.
 */
package eu.ecodex.connector.domain.model;
