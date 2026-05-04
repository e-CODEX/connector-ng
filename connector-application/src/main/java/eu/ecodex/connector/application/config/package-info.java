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
 * Contains configuration for the connector application module.
 *
 * <p>In the hexagonal architecture of the connector, the application module coordinates the domain
 * model with inbound and outbound adapters. This package provides the configuration required to
 * assemble that application layer, wire use-case services, and expose the ports needed by the
 * surrounding infrastructure.
 *
 * <p>Configuration in this package should focus on application orchestration concerns, such as:
 * <ul>
 *   <li>creating and wiring application services and use cases;
 *   <li>connecting application ports to their adapter implementations;
 *   <li>binding application properties required by the connector runtime;
 *   <li>keeping domain logic independent of framework and infrastructure-specific setup.
 * </ul>
 *
 * <p>Classes in this package should avoid implementing business rules directly. Business decisions
 * belong to the domain layer, while transport, persistence, and other technical integrations belong
 * to adapter or infrastructure modules.
 */
package eu.ecodex.connector.application.config;
