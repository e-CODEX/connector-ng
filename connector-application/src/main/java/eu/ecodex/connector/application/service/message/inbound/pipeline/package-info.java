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
 * Pipeline orchestrating the processing of inbound messages received by the connector.
 *
 * <p>The pipeline runs an ordered sequence of processing steps on each message received from the
 * gateway, such as validation, enrichment, evidence handling, persistence and routing preparation,
 * before the message is handed over for delivery to a backend. It is responsible for composing the
 * steps and controlling their execution, including how a failing step affects the rest of the run.
 *
 * <p>Classes in this package contain no processing logic of their own. Each processing concern is
 * implemented as a dedicated step in the
 * {@link eu.ecodex.connector.application.service.message.inbound.pipeline.step} subpackage, which
 * keeps steps small, independently testable and easy to reorder or extend.
 */
package eu.ecodex.connector.application.service.message.inbound.pipeline;
