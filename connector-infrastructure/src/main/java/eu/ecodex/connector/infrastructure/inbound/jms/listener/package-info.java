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
 * Provides messaging listener infrastructure for the connector.
 *
 * <p>Types in this package receive and process inbound messaging events at the infrastructure
 * boundary. They adapt transport-specific listener mechanisms to connector application services,
 * keeping message consumption concerns separate from application and domain logic.
 */
package eu.ecodex.connector.infrastructure.inbound.jms.listener;
