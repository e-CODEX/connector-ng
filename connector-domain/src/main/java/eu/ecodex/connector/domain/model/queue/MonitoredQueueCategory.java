/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.queue;

/**
 * Categories used to classify a {@link MonitoredQueue}, for example to group queues by the
 * component that owns them.
 */
public enum MonitoredQueueCategory {
    /**
     * Queue owned by the connector, such as the internal JMS queues used for message processing.
     */
    CONNECTOR,

    /**
     * Queue owned by the gateway (Domibus), such as the queues used to exchange messages with it.
     */
    GATEWAY
}
