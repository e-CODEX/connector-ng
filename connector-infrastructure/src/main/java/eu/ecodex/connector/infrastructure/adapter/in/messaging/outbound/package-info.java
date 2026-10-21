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
 * Messaging listeners consuming the messages the connector must send out, to the gateway or to a
 * backend.
 *
 * <p>Listeners in this package consume messages from messaging destinations (for example JMS
 * queues), typically those fed by the publishers of
 * {@link eu.ecodex.connector.infrastructure.adapter.out.messaging.outbound}, and call the inbound
 * ports of {@link eu.ecodex.connector.application.port.api} that start the outbound message
 * workflow. They translate the technical message (payload, headers) into domain types, so that no
 * messaging details reach the core.
 *
 * <p>Listeners here contain no business rules: they depend on the ports only, never on the
 * application services directly.
 */
package eu.ecodex.connector.infrastructure.adapter.in.messaging.outbound;
