/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.in.web.rest.request.queue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request to replay messages of the dead letter queue of a queue.
 *
 * @param queue      the name of a queue owned by the connector.
 * @param messageIds the JMS identifiers of the DLQ messages to replay (1 to 100).
 */
public record ConnectorReplayQueueDlqMessagesRequest(
    @NotBlank(message = "The [queue] must not be blank")
    String queue,

    @NotEmpty(message = "The [messageIds] must not be empty")
    @Size(max = 100, message = "The [messageIds] must not contain more than 100 identifiers")
    List<@NotBlank(message = "The [messageIds] must not contain a blank identifier") String>
        messageIds
) {
}
