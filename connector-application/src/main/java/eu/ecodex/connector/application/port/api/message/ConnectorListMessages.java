/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.api.message;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;

/**
 * Represents a contract for retrieving a paginated list of connector messages. The implementers of
 * this interface are responsible for defining the logic to retrieve the messages based on the given
 * pagination request.
 */
public interface ConnectorListMessages {
    /**
     * Executes a paginated process to retrieve a list of connector messages based on the specified
     * page request.
     *
     * @param pageRequest              the pagination request containing parameters for retrieving a
     *                                 specific page of connector messages. Must not be null.
     * @param identifier               the identifier of the message to be retrieved (id,
     *                                 ebmsMessageId, conversationId, etc.)
     * @param backendName              the name of the backend to which the message belongs to.
     * @param businessDomainIdentifier the identifier of the business domain to which the message
     *                                 belongs to.
     * @param service                  the service name of the message.
     * @param action                   the action name of the message.
     *
     * @return a {@link ConnectorPageResult} containing a list of {@link ConnectorBusinessMessage}
     *     objects and pagination metadata.
     */
    ConnectorPageResult<ConnectorBusinessMessage> execute(
        @Nonnull ConnectorPageRequest pageRequest,
        String identifier,
        String backendName,
        String businessDomainIdentifier,
        String service,
        String action
    );
}
