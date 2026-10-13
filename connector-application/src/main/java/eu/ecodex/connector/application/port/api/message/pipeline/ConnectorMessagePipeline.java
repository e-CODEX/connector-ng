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
import jakarta.annotation.Nonnull;

/**
 * Represents a processing pipeline for handling {@link ConnectorBusinessMessage} instances.
 *
 * <p>This interface defines a single method for processing messages within the pipeline.
 * Implementations are responsible for applying specific logic or transformations to the message as
 * part of a larger workflow.
 *
 * <p>The pipeline coordinates the flow of {@link ConnectorBusinessMessage} objects through various
 * stages, ensuring compliance with business and technical requirements. Each implementation may
 * encapsulate one or more processing steps, such as validation, transformation, routing, or
 * persistence (see {@link ConnectorMessageStep}).
 */
public interface ConnectorMessagePipeline {

    /**
     * Processes the given message through the pipeline.
     *
     * <p>The method returns normally if the message was processed successfully. If processing
     * fails, the failure is reported by an exception.
     *
     * @param message the business message to process (must not be null)
     */
    void process(@Nonnull ConnectorBusinessMessage message);
}
