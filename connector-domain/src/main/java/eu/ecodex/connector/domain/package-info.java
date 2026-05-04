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
 * <p>This package defines the business entities and value objects that represent the core concepts
 * of the Connector. These classes are independent of infrastructure concerns such as persistence or
 * transport and focus purely on the domain logic.
 *
 * <p>Typical responsibilities include:
 * <ul>
 *   <li>Representation of messages and their metadata</li>
 *   <li>Domain-specific identifiers and value objects</li>
 *   <li>Business rules and validation logic</li>
 * </ul>
 *
 * <p>The domain layer should remain stable and should not depend on other layers such as
 * infrastructure or application services.
 */
package eu.ecodex.connector.domain;
