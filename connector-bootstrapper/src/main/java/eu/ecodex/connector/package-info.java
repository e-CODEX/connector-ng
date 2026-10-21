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
 * Entry point of the e-CODEX Connector application.
 *
 * <p>This root package holds the Spring Boot bootstrap class that starts the connector runtime.
 * It is the only place where the layers of the hexagonal architecture are assembled: the domain
 * model, the application layer (use cases and ports) and the adapters are wired together at
 * startup, with dependencies pointing inwards towards the core.
 *
 * <p>This package contains startup code only: it must not contain business rules, use cases or
 * adapter implementations.
 */
package eu.ecodex.connector;
