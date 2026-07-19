/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.transport;

import eu.ecodex.connector.application.port.api.transport.command.UpdateMessageTransportCommand;
import jakarta.annotation.Nonnull;

/**
 * Interface defining the contract for acknowledging and updating the transport status of messages
 * within the connector system.
 *
 * <p>The primary responsibility of this interface is to facilitate the processing and updating of
 * transport-related information for messages identified by a unique identifier. This is essential
 * for ensuring that the transport status and any associated errors are accurately recorded and
 * processed within the connector system.
 */
public interface ConnectorAckMessageTransportStep {
    /**
     * Executes the update of a message's transport status in the connector system. This method
     * processes the specified command to update the transportation details of a message identified
     * by its unique identifier or Ebms ID.
     *
     * @param messageOrRemoteSystemIdentifier a unique identifier representing the message or ebms
     *                                        ID within the connector system. Must not be null.
     * @param command                         the {@link UpdateMessageTransportCommand} containing
     *                                        the transport status information and any associated
     *                                        errors for the message. Must not be null.
     */
    void execute(
        @Nonnull String messageOrRemoteSystemIdentifier,
        @Nonnull UpdateMessageTransportCommand command);
}
