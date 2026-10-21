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
 * Processing steps of the inbound message pipeline.
 *
 * <p>Each class in this package implements one stage of the pipeline defined in the parent
 * package, such as validation, enrichment, evidence handling, persistence, routing preparation or
 * hand-over for delivery. A step has a single, well-defined responsibility, so that it can be
 * tested in isolation and the pipeline can be reordered or extended by adding or replacing steps.
 *
 * <p>Steps are implementation details of the application services: other layers must not depend
 * on them. They orchestrate their stage by delegating business rules to the domain model and
 * technical concerns to outbound ports, and they are composed and executed only by the pipeline.
 */
package eu.ecodex.connector.application.service.message.inbound.pipeline.step;
