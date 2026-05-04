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
 * Contains domain logic related to message routing.
 *
 * <p>This package defines how messages are evaluated and directed to their appropriate
 * destinations based on domain-specific rules and metadata. Routing decisions are part of the
 * business logic and are independent of the underlying transport or infrastructure mechanisms.
 *
 * <p>Typical responsibilities include:
 * <ul>
 *   <li>Determining message destinations</li>
 *   <li>Evaluating routing rules and conditions</li>
 *   <li>Representing routing configurations and decisions</li>
 * </ul>
 * </p>
 *
 * <p>The routing logic should remain deterministic and testable and should not depend on external
 * systems or frameworks.
 */
package eu.ecodex.connector.domain.routing;
