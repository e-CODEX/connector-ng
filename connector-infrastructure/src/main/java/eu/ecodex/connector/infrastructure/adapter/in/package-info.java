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
 * Inbound adapters of the connector: the entry points through which the outside world drives the
 * core.
 *
 * <p>Adapters in this package (for example REST/SOAP controllers or JMS listeners) receive external
 * requests and events, translate them from their technical representation (HTTP payloads, JMS
 * messages) into domain types, and call the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}. They then translate the result, or any
 * exception raised by the core, into a transport-specific response.
 *
 * <p>Inbound adapters depend on the ports only, never on the application services directly, and
 * contain no business rules.
 */
package eu.ecodex.connector.infrastructure.adapter.in;
