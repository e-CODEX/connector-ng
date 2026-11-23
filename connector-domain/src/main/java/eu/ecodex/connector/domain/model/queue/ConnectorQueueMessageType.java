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
 * The type of JMS message held in a queue.
 */
public enum ConnectorQueueMessageType {
    /**
     * A text message, such as the JSON of a connector message.
     */
    TEXT,

    /**
     * A map message, such as the ones sent by the gateway.
     */
    MAP,

    /**
     * A bytes message.
     */
    BYTES,

    /**
     * An object message.
     */
    OBJECT,

    /**
     * A stream message.
     */
    STREAM,

    /**
     * Any other kind of message.
     */
    UNKNOWN
}
