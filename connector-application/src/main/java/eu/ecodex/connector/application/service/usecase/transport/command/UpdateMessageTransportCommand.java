/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.service.usecase.transport.command;

import eu.ecodex.connector.domain.model.message.ConnectorMessageError;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import java.util.List;
import lombok.Builder;

/**
 * Command to update the transport status of a message in the connector system.
 *
 * <p>This record encapsulates information required for updating the state of a message
 * transport operation, along with any associated errors, to ensure accurate tracking and processing
 * within the system.
 *
 * @param remoteMessageIdentifier A unique identifier for the message in the external system.
 *                                Typically used to correlate with backend system records.
 * @param status                  The current status of the message transport process, represented
 *                                by {@link ConnectorMessageTransportStatus}, which defines various
 *                                lifecycle states.
 * @param errors                  A list of {@link ConnectorMessageError} objects describing any
 *                                issues encountered during the transport process. This is used for
 *                                error tracking and diagnostic purposes.
 */
@Builder(toBuilder = true)
public record UpdateMessageTransportCommand(
        // ex: backend system message id
        String remoteMessageIdentifier,
        ConnectorMessageTransportStatus status,
        List<ConnectorMessageError> errors
) {
}
