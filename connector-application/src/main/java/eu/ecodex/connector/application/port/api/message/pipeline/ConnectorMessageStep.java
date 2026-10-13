/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.pipeline;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines a processing step in the connector message workflow.
 *
 * <p>This interface is intended to process {@link ConnectorMessage} objects and return a
 * modified or new instance of a {@link ConnectorMessage}. Implementations can represent different
 * stages of message handling, such as validation, transformation, or routing.
 *
 * <p>The processing step is a core element in the message lifecycle, ensuring that business or
 * technical requirements are fulfilled at each stage. Because the input and output types are
 * generic, a step may also convert a message from one type to another.
 *
 * @param <I> the type of message accepted by this step
 * @param <O> the type of message produced by this step
 */
public interface ConnectorMessageStep<I extends ConnectorMessage, O extends ConnectorMessage> {

    /**
     * Executes this processing step on the given message and returns the resulting message.
     *
     * <p>This method performs a specific operation on the provided message, such as validation,
     * transformation, or enrichment. The input message is not modified directly; instead, a new
     * instance or a modified copy is returned.
     *
     * @param message the message to process (must not be null)
     *
     * @return the message resulting from the processing; a new or modified instance
     *
     * @throws NullPointerException if {@code message} is {@code null}
     */
    O execute(@Nonnull I message);
}
