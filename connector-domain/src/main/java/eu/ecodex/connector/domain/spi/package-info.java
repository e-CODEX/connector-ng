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
 * Defines service provider interfaces for extending or integrating with the e-CODEX Connector
 * domain layer.
 *
 * <p>This package contains contracts implemented by external or infrastructure-specific components
 * that provide domain services to the Connector. The interfaces in this package allow the domain
 * layer to express required capabilities without depending on concrete implementations.
 *
 * <p>Typical responsibilities include:
 * <ul>
 *   <li>Providing domain-level services through stable abstractions</li>
 *   <li>Decoupling core domain logic from infrastructure and integration details</li>
 *   <li>Supporting replaceable implementations for connector-specific behaviour</li>
 * </ul>
 *
 * <p>Implementations of these interfaces should be supplied by infrastructure modules, while this
 * package should remain focused on the contracts required by the domain model.
 */
package eu.ecodex.connector.domain.spi;
