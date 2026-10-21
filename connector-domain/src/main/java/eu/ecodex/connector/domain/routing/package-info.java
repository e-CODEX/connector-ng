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
 * Domain logic for routing messages to their destinations.
 *
 * <p>This package holds the business rules that decide where a message must be directed, based on
 * its metadata and the routing configuration (for example its business domain, service and action,
 * or the link partners and backends configured for them). Routing decisions are business decisions:
 * they are expressed in domain terms and are independent of transport and infrastructure.
 *
 * <p>Typical content:
 *
 * <ul>
 *   <li>routing rules and the conditions they evaluate;
 *   <li>the representation of routing configurations and routing decisions;
 *   <li>domain services that determine the destination of a message.
 * </ul>
 *
 * <p>Routing logic is deterministic and free of framework and infrastructure dependencies, so it
 * can be tested in isolation. The application layer, in the routing services, orchestrates it and
 * supplies the configuration it needs through outbound ports.
 */
package eu.ecodex.connector.domain.routing;
