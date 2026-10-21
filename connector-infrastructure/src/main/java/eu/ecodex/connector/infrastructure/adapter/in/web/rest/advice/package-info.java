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
 * Global exception handlers of the connector's REST API.
 *
 * <p>Classes in this package are {@code @RestControllerAdvice} components that intercept
 * exceptions thrown while processing a request and translate them into HTTP error responses with an
 * appropriate status code and a consistent error body. This keeps error-handling code out of the
 * controllers of {@link eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller}.
 *
 * <p>Handlers map the exceptions raised by the core (see
 * {@link eu.ecodex.connector.application.exception}) and by the web layer (validation failures,
 * malformed requests) to client-facing errors, exposing only business-level messages and error
 * codes, never stack traces or internal details. They contain no business rules.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.rest.advice;
