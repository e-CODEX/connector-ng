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
 * Ports of the connector's application layer, following the hexagonal architecture.
 *
 * <p>Ports are the interfaces through which the core communicates with the outside world. They are
 * defined by the application and domain layers, in domain terms, and are split by direction:
 *
 * <ul>
 *   <li>{@link eu.ecodex.connector.application.port.api} contains the inbound ports: the use cases
 *       offered to inbound adapters (REST, JMS, etc.) to drive the core;
 *   <li>{@link eu.ecodex.connector.application.port.spi} contains the outbound ports: the
 *       capabilities the core requires from outbound adapters (persistence, gateway, etc.).
 * </ul>
 *
 * <p>This package holds no interfaces of its own. Dependencies always point inwards: adapters
 * depend on these ports, never the other way round.
 */
package eu.ecodex.connector.application.port;
