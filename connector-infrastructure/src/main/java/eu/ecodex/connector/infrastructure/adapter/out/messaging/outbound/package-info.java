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
 * Outbound messaging publishers for messages the connector sends out.
 *
 * <p>Classes in this package implement the messaging-related outbound ports of
 * {@link eu.ecodex.connector.application.port.spi} that publish events or messages about the
 * sending of a message, to the gateway or to a backend, to messaging destinations (for example JMS
 * queues). They translate domain types into the technical message (payload), so that no messaging
 * details reach the core.
 *
 * <p>Publishers here contain no business rules: they only hand the message over to the broker.
 * What happens to it next belongs to the listeners of
 * {@link eu.ecodex.connector.infrastructure.adapter.in.messaging.outbound} and the application
 * layer behind them.
 */
package eu.ecodex.connector.infrastructure.adapter.out.messaging.outbound;
