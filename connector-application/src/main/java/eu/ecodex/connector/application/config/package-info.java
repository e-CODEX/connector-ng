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
 * Spring configuration that assembles the application layer of the connector.
 *
 * <p>In the hexagonal architecture of the connector, the application module orchestrates the
 * domain model through its use cases, exposing inbound ports and depending on outbound ports. This
 * package holds the configuration that wires those pieces together at startup, so that the core
 * stays free of framework and infrastructure concerns.
 *
 * <p>Typical responsibilities of this package:
 *
 * <ul>
 *   <li>declaring application services and use-case beans;
 *   <li>binding outbound ports to their adapter implementations;
 *   <li>binding the configuration properties required by the application layer;
 *   <li>defining cross-cutting application concerns such as transaction boundaries.
 * </ul>
 *
 * <p>This package must not contain business rules. Business decisions belong to the domain layer,
 * while transport, persistence and other technical integrations belong to the adapter modules.
 */
package eu.ecodex.connector.application.config;
