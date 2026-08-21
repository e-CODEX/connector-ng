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

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Defines a processing step in the connector message workflow.
 *
 * <p>This interface is intended to process {@link ConnectorBusinessMessage} objects and return a
 * modified or new instance of {@link ConnectorBusinessMessage}. Implementations of this interface
 * can represent different stages of message handling, such as validation, transformation, or
 * routing.
 *
 * <p>The processing step is a core element in the message lifecycle, ensuring that business or
 * technical requirements are fulfilled at each stage.
 */
public interface ConnectorMessageStep<I extends ConnectorMessage, O extends ConnectorMessage> {
    /**
     * Executes a processing step on the given connector message and returns the resulting message.
     *
     * <p>This method is intended to perform a specific operation on the provided
     * {@code ConnectorMessage}, such as validation, transformation, or enrichment. The input
     * message is not modified directly; instead, a new instance or a modified copy of the
     * {@code ConnectorMessage} is returned.
     *
     * @param message the {@link ConnectorMessage} to process; must not be {@code null}
     *
     * @return a new or modified {@link ConnectorMessage} instance as the result of the
     *     processing
     *
     * @throws NullPointerException if the {@code message} parameter is {@code null}
     */
    O execute(@Nonnull I message);
}
