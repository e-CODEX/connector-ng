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
 * Inbound ports of the connector: the use cases the application offers to the outside world.
 *
 * <p>Each interface in this package describes a business operation from the application's point
 * of view, in domain terms and independent of how it is triggered. Inbound adapters (REST, JMS,
 * etc.) call these ports to drive the core; the application services behind them orchestrate the
 * domain model and rely on outbound ports for anything technical.
 *
 * <p>Subpackages group use cases by functional area: messages, evidences, attachments, routing,
 * links, business domains and P-Mode handling.
 *
 * <p>Interfaces in this package should use domain types in their signatures and must not expose
 * transport, persistence or framework details.
 */
package eu.ecodex.connector.application.port.api;
