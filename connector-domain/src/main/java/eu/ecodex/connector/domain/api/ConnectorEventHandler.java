/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import jakarta.annotation.Nonnull;


/**
 * Represents a handler for processing {@link ConnectorMessage} events within the connector system.
 *
 * <p>This interface defines the contract for handling incoming {@link ConnectorMessage}
 * instances. Implementations of this interface are expected to process the provided messages and
 * perform necessary operations such as validation, transformation, or further distribution within
 * the processing workflow.
 *
 * <p>Thread Safety:
 * Implementations of this interface should ensure thread safety if instances are expected to be
 * used in a multithreaded environment.
 */
public interface ConnectorEventHandler {
    /**
     * Handles the given {@link ConnectorMessage} to facilitate processing, validation, or other
     * operations required by the connector system.
     *
     * <p>This method is designed to process incoming messages represented by
     * {@link ConnectorMessage}. Implementations of this method should define specific behaviours
     * for handling these messages, including tasks such as validation, transformation, or routing
     * within the system.
     *
     * @param message The {@link ConnectorMessage} to be processed. Must not be null.
     */
    void handle(@Nonnull ConnectorMessage message);
}
