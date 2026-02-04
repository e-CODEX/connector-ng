/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents an error encountered during the processing of a connector message.
 *
 * <p>This record encapsulates the details of a specific error that occurred while
 * handling a message in the system. It includes information about the error message, additional
 * context, error codes, and metadata related to the source and processing stage where the error was
 * generated. This serves as a structured way to report and track errors for diagnostic and
 * resolution purposes.
 *
 * @param message   The main description of the error. Provides a human-readable explanation of the
 *                  issue.
 * @param details   Additional information regarding the error. This may include stack traces,
 *                  system logs, or debugging messages that help identify its root cause.
 * @param errorCode A machine-readable code representing the specific error type. This can be used
 *                  programmatically to categorize and process errors.
 * @param source    Specifies the origin or component in the system that generated the error, such
 *                  as a module or subsystem.
 * @param step      The specific processing step where the error occurred. This helps in localizing
 *                  the error to a particular stage of message handling.
 * @param processor Identifies the processor or service responsible for the task where the error
 *                  happened.
 * @param createdAt The timestamp indicating when the error was recorded. This provides temporal
 *                  context for the error's occurrence.
 */
public record ConnectorMessageError(
        String message,
        String details,
        String errorCode,
        String source,
        String step,
        String processor,
        Instant createdAt
) implements Serializable {
}
