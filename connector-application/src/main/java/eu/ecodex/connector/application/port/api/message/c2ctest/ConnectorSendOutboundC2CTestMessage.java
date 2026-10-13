/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message.c2ctest;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import jakarta.annotation.Nonnull;

/**
 * Use case for sending outbound connector-to-connector (C2C) test business messages.
 *
 * <p>The messages allow checking that a link partner can be reached and that the message exchange
 * works end to end.
 */
public interface ConnectorSendOutboundC2CTestMessage {
    /**
     * Creates and sends an outbound test business message as described by the given command.
     *
     * @param command the {@link ConnectorTestBusinessMessageCommand} describing the test message to
     *                send (must not be null)
     *
     * @return the {@link ConnectorBusinessMessage} that was created and sent
     */
    ConnectorBusinessMessage execute(@Nonnull ConnectorTestBusinessMessageCommand command);
}
