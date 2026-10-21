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
 * Startup initializers of the connector's infrastructure.
 *
 * <p>Classes in this package run when the application starts, after the Spring context has been
 * assembled, to bring the technical environment into a usable state: for example loading or seeding
 * initial data (default users, a P-Mode set), preparing directories or keystores, and checking that
 * required resources and settings are available. Failures that would make the connector unusable
 * should stop the startup rather than surface later at runtime.
 *
 * <p>Initializers are idempotent and safe to run on every startup. They perform technical
 * preparation only and contain no business rules: decisions about what the initial data means
 * belong to the domain model and the application services, which initializers call through the
 * inbound ports of {@link eu.ecodex.connector.application.port.api}.
 */
package eu.ecodex.connector.infrastructure.initializer;
