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
 * Outbound adapters of the connector: the implementations through which the core reaches the
 * outside world.
 *
 * <p>Adapters in this package (for example persistence, gateway communication or messaging)
 * implement the outbound ports of {@link eu.ecodex.connector.application.port.spi} using concrete
 * technologies. They translate between domain types and external representations (database rows,
 * gateway messages, JMS payloads), so that technical details never reach the core.
 *
 * <p>Outbound adapters are plugged into the application layer at startup and contain no business
 * rules. Dependencies point inwards: they depend on the ports and the domain types, never the other
 * way round.
 */
package eu.ecodex.connector.infrastructure.adapter.out;
