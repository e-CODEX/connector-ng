/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.pipeline;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;

/**
 * Represents a processing pipeline for handling {@link ConnectorMessage} instances.
 *
 * <p>This interface defines a single method for processing messages within the pipeline.
 * Implementations of this interface are responsible for applying specific logic or
 * transformations to the message as part of a larger workflow.
 *
 * <p>The processing pipeline plays a critical role in coordinating the flow of
 * {@link ConnectorMessage} objects through various stages, ensuring compliance with
 * business and technical requirements.
 *
 * <p>Each implementation of the pipeline may encapsulate one or more processing steps,
 * such as validation, transformation, routing, or persistence.
 */
public interface ConnectorMessagePipeline {
    void process(@Nonnull ConnectorMessage message);
}
