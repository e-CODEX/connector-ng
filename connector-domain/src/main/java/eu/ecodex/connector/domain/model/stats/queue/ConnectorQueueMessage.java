/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.stats.queue;

import java.time.Instant;
import java.util.Map;

/**
 * Describes a message held in a monitored queue or in its dead letter queue (DLQ).
 *
 * <p>Only the headers and the properties of the message are exposed, not its body, as the body may
 * carry business payloads: see {@link ConnectorQueueMessageContent} to read it.
 *
 * @param messageId     the JMS message identifier.
 * @param timestamp     the time the message was originally sent to the queue; may be {@code null}.
 * @param correlationId the JMS correlation identifier; may be {@code null}.
 * @param messageType   the JMS message type.
 * @param redelivered   whether the message was redelivered at least once.
 * @param deliveryCount the number of delivery attempts, if known, otherwise {@code 0}.
 * @param failureCause  the reason why the broker moved the message to the DLQ; {@code null} for
 *                      messages which are not in a DLQ.
 * @param properties    the message properties rendered as strings; never {@code null}.
 */
public record ConnectorQueueMessage(
    String messageId,
    Instant timestamp,
    String correlationId,
    ConnectorQueueMessageType messageType,
    boolean redelivered,
    int deliveryCount,
    String failureCause,
    Map<String, String> properties
) {
    /**
     * Canonical constructor making the properties immutable and never {@code null}.
     */
    public ConnectorQueueMessage {
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }
}
