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
 * Application services implementing the connector's use cases.
 *
 * <p>Each service implements one or more inbound ports from
 * {@link eu.ecodex.connector.application.port.api}. It orchestrates a business workflow by loading
 * and persisting domain objects through outbound ports
 * ({@link eu.ecodex.connector.application.port.spi}) and delegating business rules and validations
 * to the domain model.
 *
 * <p>Services in this package coordinate; they do not decide. Business rules belong to the domain
 * layer, and technical concerns such as transport or persistence belong to the adapters. Services
 * depend on ports only, never on concrete adapter implementations.
 */
package eu.ecodex.connector.application.service;
