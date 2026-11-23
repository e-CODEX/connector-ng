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

import java.util.List;

/**
 * Outcome of the replay of dead letter queue (DLQ) messages.
 *
 * @param replayed the identifiers of the messages sent back to their original queue and removed
 *                 from the DLQ.
 * @param notFound the identifiers which are not (or no longer) in the DLQ.
 * @param failed   the identifiers of the messages which could not be replayed, and were left in
 *                 the DLQ.
 */
public record ConnectorQueueReplayResult(
    List<String> replayed,
    List<String> notFound,
    List<String> failed
) {
    /**
     * Canonical constructor making the lists immutable and never {@code null}.
     */
    public ConnectorQueueReplayResult {
        replayed = replayed == null ? List.of() : List.copyOf(replayed);
        notFound = notFound == null ? List.of() : List.copyOf(notFound);
        failed = failed == null ? List.of() : List.copyOf(failed);
    }
}
