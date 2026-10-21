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
 * Inbound web adapters of the connector: the HTTP entry points (REST and SOAP) through which the
 * outside world drives the core.
 *
 * <p>Spring MVC controllers and SOAP endpoints in this package receive HTTP requests, validate
 * and translate their payloads into domain types, and call the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}. They then translate the result, or any
 * exception raised by the core, into an HTTP response (REST) or a SOAP response or fault.
 *
 * <p>Endpoints here contain no business rules: they depend on the ports only, never on the
 * application services directly, and keep protocol concerns (request and response DTOs, status
 * codes, SOAP envelopes and faults, content negotiation) out of the core.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web;
