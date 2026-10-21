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
 * Translation of exceptions into HTTP error responses for the REST API.
 *
 * <p>Global handlers map core and web-layer exceptions to status codes and a consistent error
 * body; no stack traces or internal details are exposed.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.rest.exception;
