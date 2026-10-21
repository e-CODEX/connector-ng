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
 * Exception types signaling business rule violations in the connector's domain concepts.
 *
 * <p>These exceptions cover violations related to messages, attachments, business domains, link
 * partners, processing modes, services, actions, evidence handling and gateway submission. They let
 * the core report failures in business terms, without depending on transport, persistence or any
 * other infrastructure concern.
 *
 * <p>Inbound adapters (REST, JMS, etc.) are expected to catch these exceptions and translate them
 * into transport-specific responses or user-facing error messages. Outbound adapters should map
 * technical failures to these types only when a business-level meaning exists.
 */
package eu.ecodex.connector.application.exception;
