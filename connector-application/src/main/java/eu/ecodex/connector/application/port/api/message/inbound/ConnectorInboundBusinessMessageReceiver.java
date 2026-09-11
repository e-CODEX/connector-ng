/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.inbound;

import jakarta.annotation.Nonnull;

/**
 * This interface defines the contract for processing inbound business messages received by the
 * connector system. Implementations of this interface are responsible for handling the execution of
 * operations related to the processing of inbound business messages, such as verification,
 * validation, and any necessary transformations.
 */
public interface ConnectorInboundBusinessMessageReceiver {
    /**
     * Executes the processing of an inbound business message within the connector system. This
     * method is responsible for handling all operations required for processing and validating the
     * input message according to the system's functional requirements.
     *
     * @param command A {@code ConnectorInboundBusinessMessageCommand} object encapsulating details
     *                of the inbound business message to be processed.
     */
    void execute(@Nonnull ConnectorInboundBusinessMessageCommand command);
}
