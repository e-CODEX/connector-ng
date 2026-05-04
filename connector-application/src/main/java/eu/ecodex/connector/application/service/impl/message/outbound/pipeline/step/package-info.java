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
 * Contains pipeline step implementations used while processing outbound messages.
 *
 * <p>Classes in this package represent individual stages of the outbound message pipeline.
 * Each step is responsible for a distinct part of the processing flow, such as validation,
 * transformation, persistence, routing, or handover to later application services.
 *
 * <p>The package is part of the application service layer and should contain implementation
 * details only. Public contracts used by other layers should be defined outside this implementation
 * package.
 */
package eu.ecodex.connector.application.service.impl.message.outbound.pipeline.step;
