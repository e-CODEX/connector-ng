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
 * Core domain model of the e-CODEX Connector.
 *
 * <p>This package is the heart of the hexagonal architecture: it expresses the connector's
 * business concepts (messages and their metadata, attachments, evidences, link partners, business
 * domains, P-Modes) as entities, value objects and domain services, together with the business
 * rules and validations that apply to them.
 *
 * <p>The domain layer is self-contained and framework-agnostic. It depends on no other layer:
 * not on the application layer, and not on persistence, transport or any other infrastructure.
 * Other layers depend on it, never the other way round.
 */
package eu.ecodex.connector.domain;
