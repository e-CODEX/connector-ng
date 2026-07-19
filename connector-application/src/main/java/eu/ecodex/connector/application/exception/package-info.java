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
 * Provides domain-level exception types used by the connector domain model and domain services.
 *
 * <p>The exceptions in this package represent business and domain rule violations related to
 * connector concepts such as messages, attachments, business domains, link partners, processing
 * modes, services, actions, evidence handling, and gateway submission.
 *
 * <p>These exception types are intended to keep domain error handling explicit and independent
 * of infrastructure concerns. Application and infrastructure layers may catch and translate them
 * into transport-specific responses, persistence errors, or user-facing error messages.
 */
package eu.ecodex.connector.application.exception;
