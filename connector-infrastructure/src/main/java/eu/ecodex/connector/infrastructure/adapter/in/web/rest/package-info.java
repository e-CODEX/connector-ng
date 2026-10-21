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
 * Inbound REST adapters of the connector: the HTTP endpoints exposing its functionality as a REST
 * API.
 *
 * <p>Spring MVC controllers in this package receive HTTP requests, validate and translate their
 * payloads into domain types, and call the inbound ports of
 * {@link eu.ecodex.connector.application.port.api}. They then translate the result, or any
 * exception raised by the core, into an HTTP response with the appropriate status code.
 *
 * <p>Controllers here contain no business rules: they depend on the ports only, never on the
 * application services directly, and keep HTTP concerns (request and response DTOs, status codes,
 * content negotiation) out of the core.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.rest;
