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
 * Provides SOAP-based inbound web adapters for receiving external requests into the connector.
 *
 * <p>Types in this package expose and support SOAP web service endpoints at the infrastructure
 * boundary. They are responsible for translating inbound SOAP messages into application-level
 * requests while keeping transport-specific concerns isolated from the core connector logic.
 */
package eu.ecodex.connector.infrastructure.inbound.web.soap;
