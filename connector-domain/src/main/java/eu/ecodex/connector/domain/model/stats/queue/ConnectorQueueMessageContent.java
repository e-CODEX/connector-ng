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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A message held in a monitored queue or in its dead letter queue (DLQ), with its body.
 *
 * <p>The body may carry business payloads: it is only exposed on request, for one message at a
 * time.
 *
 * <p>The connector queues hold {@code ConnectorPublishable} messages serialized as JSON text, which
 * is returned as a {@code payload} map; so are the entries of a map message, as sent by the
 * gateway. Any other text, which is not a JSON object, is returned as {@code text}.
 *
 * @param message   the headers and the properties of the message.
 * @param payload   the body as a map: the JSON object of a text message, or the entries of a map
 *                  message; {@code null} when the body is not a map.
 * @param text      the body of a text message which is not a JSON object, or which is too large to
 *                  be read as one; {@code null} otherwise.
 * @param truncated whether the body was cut because it exceeds the size which can be returned.
 */
public record ConnectorQueueMessageContent(
    ConnectorQueueMessage message,
    Map<String, Object> payload,
    String text,
    boolean truncated
) {
    /**
     * Canonical constructor making the payload read-only.
     */
    public ConnectorQueueMessageContent {
        payload = payload == null
            ? null : Collections.unmodifiableMap(new LinkedHashMap<>(payload));
    }
}
