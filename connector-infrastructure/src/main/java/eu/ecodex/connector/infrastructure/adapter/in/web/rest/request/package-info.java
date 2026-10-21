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
 * Request body types of the connector's REST API.
 *
 * <p>Classes in this package define the payloads that API clients (such as the admin UI) send in
 * the body of HTTP requests handled by the controllers of
 * {@link eu.ecodex.connector.infrastructure.adapter.in.web.rest.controller}. They are part of the
 * public contract of the REST API: their shape is driven by what clients submit, not by the domain
 * model, which they deliberately do not expose.
 *
 * <p>Request bodies are plain, immutable data carriers. They declare input validation (for
 * example with Bean Validation annotations) but hold no business rules: business validation belongs
 * to the domain model. Controllers convert them into domain types or command objects before calling
 * the inbound ports of {@link eu.ecodex.connector.application.port.api}.
 */
package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request;
