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
 * Spring configuration of the connector's infrastructure layer.
 *
 * <p>Classes in this package declare and configure the technical beans the adapters rely on, such
 * as persistence, messaging (JMS), web and SOAP, security, serialization, scheduling and the DSS
 * library.
 *
 * <p>This package contains wiring and framework setup only: it must not contain business rules,
 * use cases or adapter logic. Application-level wiring (use-case beans, properties of the
 * application layer) lives in {@code eu.ecodex.connector.application.config}.
 */
package eu.ecodex.connector.infrastructure.config;
