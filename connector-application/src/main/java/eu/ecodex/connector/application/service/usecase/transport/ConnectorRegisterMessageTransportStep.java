/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.transport;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import jakarta.annotation.Nonnull;

/**
 * Represents a step in the connector registration process that processes and executes the
 * transportation of a message.
 *
 * <p>This interface defines a contract for handling the registration of a message transport step
 * within the connector system. The primary responsibility is to process a given message alongside
 * its current transport status.
 */
public interface ConnectorRegisterMessageTransportStep {
    ConnectorMessageTransportStep execute(
            @Nonnull ConnectorMessage message,
            @Nonnull ConnectorMessageTransportStatus status);
}
