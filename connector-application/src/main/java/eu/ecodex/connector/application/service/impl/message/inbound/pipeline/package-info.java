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
 * Provides the inbound connector message processing pipeline.
 *
 * <p>This package contains the pipeline orchestration responsible for handling messages received
 * by the connector before they are handed over to the application-specific processing flow. The
 * pipeline coordinates the ordered execution of inbound processing steps, such as validation,
 * enrichment, evidence handling, persistence, and routing preparation.
 *
 * <p>Classes in this package should focus on pipeline composition and execution control.
 * Individual processing concerns should be implemented as dedicated pipeline steps in the
 * corresponding {@code step} subpackage.
 */
package eu.ecodex.connector.application.service.impl.message.inbound.pipeline;
