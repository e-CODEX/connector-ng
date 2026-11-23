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
 * Application services for JMS queue monitoring and administration of the connector.
 *
 * <p>This package exposes the operational view of the connector's JMS broker. It lets
 * administrators inspect queues, browse messages and recover messages that ended up in a dead
 * letter queue, without consuming them from their normal destinations.
 */
package eu.ecodex.connector.application.service.queue;
