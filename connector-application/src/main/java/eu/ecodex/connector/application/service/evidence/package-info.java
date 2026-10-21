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
 * Application services implementing the evidence use cases of the connector.
 *
 * <p>Services in this package implement the evidence-related inbound ports of
 * {@link eu.ecodex.connector.application.port.api.evidence}. They produce the success and failure
 * evidences of connector messages and persist them through outbound ports, delegating the rules
 * that determine which evidence applies to the domain model.
 */
package eu.ecodex.connector.application.service.evidence;
